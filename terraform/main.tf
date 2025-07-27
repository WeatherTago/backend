# VPC 생성
module "vpc" {
  source             = "./modules/vpc"
  project_name       = var.project_name
  environment        = var.environment
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
}

# Security Groups 생성
module "security_groups" {
  source       = "./modules/security_groups"
  project_name = var.project_name
  environment  = var.environment
  vpc_id       = module.vpc.vpc_id
}

# EC2 인스턴스 생성
module "ec2" {
  source         = "./modules/ec2"
  project_name   = var.project_name
  environment    = var.environment
  ami_id         = var.ec2_ami_id
  instance_type  = var.ec2_instance_type
  subnet_id      = module.vpc.private_subnet_ids[0]
  sg_id          = module.security_groups.ec2_sg_id
  key_name       = var.ec2_key_name
  docker_image   = var.docker_image
  docker_tag     = var.docker_tag
  container_port = var.container_port
}

# Application Load Balancer 생성
module "alb" {
  source            = "./modules/alb"
  project_name      = var.project_name
  environment       = var.environment
  vpc_id            = module.vpc.vpc_id
  subnet_ids        = module.vpc.public_subnet_ids
  sg_id             = module.security_groups.alb_sg_id
  ec2_instance_id   = module.ec2.instance_id
  container_port    = var.container_port
}

# CloudFront 생성
module "cloudfront" {
  source       = "./modules/cloudfront"
  project_name = var.project_name
  environment  = var.environment
  alb_dns_name = module.alb.dns_name
}

# RDS 생성
module "rds" {
  source            = "./modules/rds"
  project_name      = var.project_name
  environment       = var.environment
  engine_version    = var.rds_engine_version
  instance_class    = var.rds_instance_class
  db_name           = var.rds_db_name
  db_username       = var.rds_db_username
  db_password       = var.rds_db_password
  subnet_ids        = module.vpc.private_subnet_ids
  sg_id             = module.security_groups.rds_sg_id
  allocated_storage = var.rds_allocated_storage
}

# ElastiCache 생성
module "elasticache" {
  source       = "./modules/elasticache"
  project_name = var.project_name
  environment  = var.environment
  node_type    = var.elasticache_node_type
  subnet_ids   = module.vpc.private_subnet_ids
  sg_id        = module.security_groups.elasticache_sg_id
}
