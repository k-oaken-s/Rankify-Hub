resource "aws_lb" "main" {
    name               = "${var.service_name}-lb"
    internal           = false
    load_balancer_type = "application"
    security_groups    = [aws_security_group.main.id]
    subnets            = var.subnets
}

resource "aws_lb_target_group" "main" {
    name     = "${var.service_name}-tg"
    port     = 80
    protocol = "HTTP"
    vpc_id   = var.vpc_id
}

resource "aws_lb_listener" "http" {
    load_balancer_arn = aws_lb.main.arn
    port              = 80
    protocol          = "HTTP"

    default_action {
        type             = "forward"
        target_group_arn = aws_lb_target_group.main.arn
    }
}

resource "aws_security_group" "main" {
    name_prefix = "${var.service_name}-sg"
    vpc_id      = var.vpc_id

    ingress {
        from_port   = 80
        to_port     = 80
        protocol    = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    egress {
        from_port   = 0
        to_port     = 0
        protocol    = "-1"
        cidr_blocks = ["0.0.0.0/0"]
    }
}
