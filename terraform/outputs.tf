# Data source pour relire les infos de l’ACR créé
data "azurerm_container_registry" "acr" {
  name                = azurerm_container_registry.acr.name
  resource_group_name = azurerm_resource_group.rg.name
}

# Login server (ex: myacrregistry1234.azurecr.io)
output "acr_login_server" {
  value = data.azurerm_container_registry.acr.login_server
}

# Admin username
output "acr_admin_username" {
  value     = data.azurerm_container_registry.acr.admin_username
  sensitive = true
}

# Mot de passe primaire (l’ACR en a 2)
output "acr_admin_password_primary" {
  value     = data.azurerm_container_registry.acr.admin_password
  sensitive = true
}
