terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "4.26.0"
    }
  }
}


provider "azurerm" {
    features {}
}

#Crée un groupe de ressources Azure pour regrouper et gérer les ressources liées entre elles.
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

#Crée une identité managée pouvant être assignée à une ou plusieurs ressources pour l’authentification.
resource "azurerm_user_assigned_identity" "identity" {
  name                = var.identity_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
}

#Crée un registre de conteneurs Azure (ACR) pour stocker et gérer des images Docker.
resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

#Attribue un rôle spécifique (comme AcrPull) à une identité sur une ressource pour lui accorder des permissions précises.
resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.acr.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.identity.principal_id
}


#Définit un plan App Service qui spécifie les ressources (CPU/RAM/prix) utilisées par les applications web.
resource "azurerm_service_plan" "app_service_plan" {
  name                = var.service_plan_name
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  os_type             = "Linux"
  sku_name            = "B1"
}


#Crée une application web Azure basée sur Linux, pour l'app spring avec son image Docker.
resource "azurerm_linux_web_app" "server_app" {
  name                = var.server_app_name
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  service_plan_id     = azurerm_service_plan.app_service_plan.id
  site_config {
    container_registry_use_managed_identity       = true
    container_registry_managed_identity_client_id = azurerm_user_assigned_identity.identity.client_id

    application_stack {
      docker_image_name   = var.spring_docker_image_name
      docker_registry_url = "https://${azurerm_container_registry.acr.login_server}"
    }
  }

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.identity.id]
  }

  app_settings = {
    WEBSITES_PORT = var.server_app_port
    APP_FRONTEND_URL = "https://${var.client_app_name}.azurewebsites.net"
  }
}

#Crée une application web Azure basée sur Linux, pour la vue client avec son image Docker.
resource "azurerm_linux_web_app" "client_app" {
  name                = var.client_app_name
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  service_plan_id     = azurerm_service_plan.app_service_plan.id
  site_config {
    container_registry_use_managed_identity       = true
    container_registry_managed_identity_client_id = azurerm_user_assigned_identity.identity.client_id

    application_stack {
      docker_image_name   = var.vuejs_docker_image_name
      docker_registry_url = "https://${azurerm_container_registry.acr.login_server}"
    }
  }

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.identity.id]
  }

  app_settings = {
    WEBSITES_PORT = var.client_app_port
    #definit l'url de l'app flask comme variable d'environnement de l'app
    SERVER_URL = "https://${azurerm_linux_web_app.server_app.default_hostname}"
  }
}