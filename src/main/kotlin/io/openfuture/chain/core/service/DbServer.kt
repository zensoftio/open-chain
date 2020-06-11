package io.openfuture.chain.core.service

import org.h2.tools.Server
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
class TCPServerConfiguration {

    val log = LoggerFactory.getLogger(TCPServerConfiguration::class.java)

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun dbTcpServer(): Server {
        log.info("Starting DB server")
        val tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092")
        return tcpServer.start()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun dbWebServer(): Server {
        log.info("Starting DB server")
        val tcpServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
        return tcpServer.start()
    }

}