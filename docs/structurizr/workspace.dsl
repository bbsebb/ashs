workspace "ASHS" "Handball Club Management and Information System" {

    !identifiers hierarchical

    model {
        publicUser = person "Public user"
        adminUser = person "Administrator"
        ss = softwareSystem "Handball Club Management and Information System" {
            authProvider = container "Keycloak" "Provides authentification and authorization"
            publicFrontend = container "public-fronted" {
                description "The public-facing website for the handball club, providing information about the club, social media updates, and a contact form."
                technology "Angular"
                tags "angular frontend web"
                contactForm = component "contact-form" {
                    description "Form to send a message to contact"
                    technology "Angular"
                }
                instagramView = component "instagram-view" {
                    description "View for social media"
                    technology "Angular"
                }
                hallView = component "hall" {
                    description "View for halls"
                    technology "Angular"
                }
                trainingSessionView = component "training-session" {
                    description "View for training sessions"
                    technology "Angular"
                }
                coachView = component "coach" {
                    description "View for coachs"
                    technology "Angular"
                }
                teamView = component "team" {
                    description "View for teams"
                    technology "Angular"
                }
                feedService = component "instagram-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                hallService = component "hall-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                trainingSessionService = component "training-session-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                coachService = component "coach-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                teamService = component "team-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                contactService = component "contact-service" {
                    description "send message to contact via the API"
                    technology "Angular"
                }

            }
            adminFrontend = container "admin-fronted" {
                description "The administration website for managing club data, including teams, coaches, training schedules, and facilities. Allows authorized personnel to create, update, and delete information displayed on the public website."
                technology "Angular"
                tags "angular frontend web"
                instagramManager = component "instagram-manager" {
                    description "manage for social media"
                    technology "Angular"
                }
                hallManager = component "hall-manager" {
                    description "Manage for halls"
                    technology "Angular"
                }
                trainingSessionManager = component "training-session-manager" {
                    description "Manage for training sessions"
                    technology "Angular"
                }
                coachManager = component "coach-manager" {
                    description "Manage for coachs"
                    technology "Angular"
                }
                teamManager = component "team-manager" {
                    description "Manage for teams"
                    technology "Angular"
                }
                feedService = component "instagram-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                hallService = component "hall-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                trainingSessionService = component "training-session-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                coachService = component "coach-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
                teamService = component "team-service" {
                    description "service who retrieves data from the API"
                    technology "Angular"
                }
            }

            gatewayService = container "gateway-service" {
                description "API Gateway for all backend services, routing requests and enforcing security policies."
                technology "Spring"
                tags "spring spring-cloud backend gateway"
            }

            discoveryService = container "discovery-service" {
                description "Service registry and discovery server for all microservices, enabling dynamic service lookup."
                technology "Spring"
                tags "spring spring-cloud backend discovery"
            }

            configService = container "config-service" {
                description "Centralized configuration management service for all microservices, providing a single source of truth for configurations."
                technology "Spring"
                tags "spring spring-cloud backend config"
            }

            feedService = container "instagram-service" {
                description "Retrieves and processes Instagram posts for display on the website."
                technology "Spring"
                tags "spring spring-cloud backend"
                instagramController = component "instagramController" {
                    description "Controller for instagram"
                    technology "Spring MVC REST"
                    tags "spring mvc rest controller"
                }
                feedService = component "feedService" {
                    description "Service for instagram"
                    technology "Spring"
                    tags "spring service"
                }
                instagramRepository = component "instagramRepository" {
                    description "Repository for instagram"
                    technology "Spring Data JPA"
                    tags "spring data jpa repository"
                }
            }




            trainingService = container "training-service" {
                description "Manages training data (teams, coaches, venues, training slots) and persists data in a PostgreSQL database."
                technology "Spring"
                tags "spring spring-cloud backend"
                trainingController = component "trainingController" {
                    description "Controller for training"
                    technology "Spring MVC REST"
                    tags "spring mvc rest controller"
                }
                trainingService = component "trainingService" {
                    description "Service for training"
                    technology "Spring"
                    tags "spring service"
                }
                trainingRepository = component "trainingRepository" {
                    description "Repository for training"
                    technology "Spring Data JPA"
                    tags "spring data jpa repository"
                }
            }


            contactService = container "contact-service" {
                description "Manages sending message forward gmail "
                technology "Spring"
                tags "spring spring-cloud backend"
                emailController = component "emailController" {
                    description "Controller for email"
                    technology "Spring MVC REST"
                    tags "spring mvc rest controller"
                }
                emailService = component "emailService" {
                    description "Service for email"
                    technology "Spring"
                    tags "spring service"
                }
            }


            dbTraining = container "Postgres db training" {
                description "Postgres db for training service"
                technology "postegresql"
                tag "db psql"
            }

            dbInstagram = container "Postgres db instagram" {
                description "Postgres db for isntagram service"
                technology "postegresql"
                tag "db psql"
            }

        }

        instagram = softwareSystem "Instagram API" "Provides instagram data and services"
        googleMap = softwareSystem "Google Maps API" "Provides mapping data and services"
        emailProvider = softwareSystem "SMTP provider" "Provides sending email with gmail"

        gitHubConfigRepo = softwareSystem "GitHub Configuration Repository" "Hosts configuration files for the Config Service"


        //public user
        // User to inside
        publicUser -> ss.publicFrontend "Uses"
        publicUser -> ss.publicFrontend.instagramView "view instagram post"
        publicUser -> ss.publicFrontend.contactForm "Send message to contact"
        publicUser -> ss.publicFrontend.hallView "view halls"
        publicUser -> ss.publicFrontend.coachView "view coachs"
        publicUser -> ss.publicFrontend.trainingSessionView "view training sessions"
        publicUser -> ss.publicFrontend.teamView "view team"
        //Inside
        ss.publicFrontend.instagramView -> ss.publicFrontend.feedService "Uses"
        ss.publicFrontend.contactForm -> ss.publicFrontend.contactService "Uses"
        ss.publicFrontend.hallView -> ss.publicFrontend.hallService "Uses"
        ss.publicFrontend.trainingSessionView -> ss.publicFrontend.trainingSessionService "Uses"
        ss.publicFrontend.coachView -> ss.publicFrontend.coachService "Uses"
        ss.publicFrontend.teamView -> ss.publicFrontend.teamService "Uses"

        //Inside to API or providers
        ss.publicFrontend.feedService -> ss.gatewayService "makes API calls to" "HTTPS HAL" "api-call"
        ss.publicFrontend.hallService -> googleMap "Uses"
        ss.publicFrontend.hallService -> ss.gatewayService "makes API calls to" "HTTPS HAL" "api-call"
        ss.publicFrontend.trainingSessionService -> ss.gatewayService "makes API calls to" "HTTPS HAL" "api-call"
        ss.publicFrontend.coachService -> ss.gatewayService "makes API calls to" "HTTPS HAL" "api-call"
        ss.publicFrontend.teamService -> ss.gatewayService "makes API calls to" "HTTPS HAL" "api-call"
        ss.publicFrontend.contactService -> ss.gatewayService "makes API calls to" "HTTPS HAL" "api-call"

        // admin user
        // User to inside
        adminUser -> ss.adminFrontend "Uses"
        adminUser -> ss.adminFrontend.instagramManager "view instagram post"
        adminUser -> ss.adminFrontend.hallManager "view halls"
        adminUser -> ss.adminFrontend.coachManager "view coachs"
        adminUser -> ss.adminFrontend.trainingSessionManager "view training sessions"
        adminUser -> ss.adminFrontend.teamManager "view team"
        //Inside
        ss.adminFrontend.instagramManager -> ss.adminFrontend.feedService "Uses"
        ss.adminFrontend.hallManager -> ss.adminFrontend.hallService "Uses"
        ss.adminFrontend.trainingSessionManager -> ss.adminFrontend.trainingSessionService "Uses"
        ss.adminFrontend.coachManager -> ss.adminFrontend.coachService "Uses"
        ss.adminFrontend.teamManager -> ss.adminFrontend.teamService "Uses"
        //Inside to API or providers
        ss.adminFrontend -> ss.authProvider "Authenticates and authorizes" "JWT" "auth"
        ss.adminFrontend.feedService -> ss.gatewayService "makes API calls to" "HTTPS JSON" "api-call"
        ss.adminFrontend.hallService -> ss.gatewayService "makes API calls to" "HTTPS JSON" "api-call"
        ss.adminFrontend.trainingSessionService -> ss.gatewayService "makes API calls to" "HTTPS JSON" "api-call"
        ss.adminFrontend.coachService -> ss.gatewayService "makes API calls to" "HTTPS JSON" "api-call"
        ss.adminFrontend.teamService -> ss.gatewayService "makes API calls to" "HTTPS JSON" "api-call"


        //backend
        ss.gatewayService -> ss.feedService.instagramController "Routes requests to"
        ss.gatewayService -> ss.contactService.emailController "Routes requests to"
        ss.gatewayService -> ss.trainingService.trainingController "Routes requests to"

        ss.gatewayService -> ss.discoveryService "Registers and discovers services from"
        ss.gatewayService -> ss.configService "Fetches configurations from"

        ss.feedService -> ss.discoveryService "Registers itself to"
        ss.feedService -> ss.configService "Fetches configurations from"
        ss.feedService.instagramController -> ss.feedService.feedService "Uses"
        ss.feedService.feedService -> ss.feedService.instagramRepository "Uses"
        ss.feedService.instagramRepository -> ss.dbInstagram "Reads from and write to" "SQL"

        ss.contactService -> ss.discoveryService "Registers itself to"
        ss.contactService -> ss.configService "Fetches configurations from"
        ss.contactService.emailController -> ss.contactService.emailService "Uses"


        ss.trainingService -> ss.discoveryService "Registers itself to"
        ss.trainingService -> ss.configService "Fetches configurations from"
        ss.trainingService -> ss.authProvider "JWT validation" "JWT" "auth"
        ss.trainingService.trainingController -> ss.trainingService.trainingService "Uses"
        ss.trainingService.trainingService -> ss.trainingService.trainingRepository "Uses"
        ss.trainingService.trainingRepository -> ss.dbTraining "Reads from and write to" "SQL"

        // Linking the config service to the GitHub repository
        ss.configService -> gitHubConfigRepo "Fetches configuration files from"
        ss.contactService.emailService -> emailProvider "uses to send email"
        ss.feedService.feedService -> instagram "Uses"
    }

    views {
        systemContext ss "Diagram1" {
            include *
            autolayout lr
        }

        container ss "Diagram2" {
            include *
            autolayout lr
        }


        component ss.feedService "Diagram3" {
            include *
            autolayout
        }

        component ss.trainingService "Diagram4" {
            include *
            autolayout
        }

        component ss.contactService "Diagram5" {
            include *
            autolayout
        }

        component ss.publicFrontend "Diagram6" {
            include *
            autolayout
        }

        component ss.adminFrontend "Diagram7" {
            include *
            autolayout
        }
    }


}

