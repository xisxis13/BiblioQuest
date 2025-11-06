variable "location" {
  description = "Azure region"
  type        = string
  default     = "West Europe"
}

variable "resource_group_name" {
  description = "Name of the resource group"
  type        = string
  default     = "books-container-app"
}

variable "acr_name" {
  description = "Name of the Azure Container Registry"
  type        = string
  default     = "booksRegistery"
}

variable "identity_name" {
  description = "Name of the user-assigned managed identity"
  type        = string
  default     = "books-identity"
}

variable "service_plan_name" {
  description = "Name of the App Service plan"
  type        = string
  default     = "appserviceplan"
}

variable "server_app_name" {
  description = "Name of the spring App"
  type        = string
  default     = "books-server-app"
}

variable "client_app_name" {
  description = "Name of the flask App"
  type        = string
  default     = "books-client-app"
}

variable "spring_docker_image_name" {
  description = "Docker image name and tag"
  type        = string
  default     = "springapp:latest"
}

variable "vuejs_docker_image_name" {
  description = "Docker image name and tag"
  type        = string
  default     = "vuejsapp:latest"
}

variable "server_app_port" {
  description = "Port on which the spring boot app listens"
  type        = string
  default     = "8080"
}

variable "client_app_port" {
  description = "Port on which the vue.js app listens"
  type        = string
  default     = "80"
}