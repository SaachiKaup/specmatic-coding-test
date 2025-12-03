package com.store.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.cfg.CoercionAction

@Configuration
open class StrictPrimitiveTypeCoercionConfig {

    @Bean
    @Primary
    open fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        return builder.build<ObjectMapper>().apply {
            val inputDataTypes = listOf(
                CoercionInputShape.Integer,
                CoercionInputShape.Float,
                CoercionInputShape.Boolean
            )
            inputDataTypes.forEach {
                coercionConfigDefaults().setCoercion(it, CoercionAction.Fail)
            }
        }
    }
}
