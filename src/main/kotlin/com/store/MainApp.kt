package com.store

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature

@SpringBootApplication
open class Application {

    @Bean
    @Primary
    open fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        return builder.build<ObjectMapper>().apply {
            // Disable all type coercion - reject when wrong types are sent
            coercionConfigDefaults().setCoercion(
                com.fasterxml.jackson.databind.cfg.CoercionInputShape.Integer,
                com.fasterxml.jackson.databind.cfg.CoercionAction.Fail
            )
            coercionConfigDefaults().setCoercion(
                com.fasterxml.jackson.databind.cfg.CoercionInputShape.Float,
                com.fasterxml.jackson.databind.cfg.CoercionAction.Fail
            )
            coercionConfigDefaults().setCoercion(
                com.fasterxml.jackson.databind.cfg.CoercionInputShape.Boolean,
                com.fasterxml.jackson.databind.cfg.CoercionAction.Fail
            )
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}