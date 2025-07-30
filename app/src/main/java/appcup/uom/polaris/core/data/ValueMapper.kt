package appcup.uom.polaris.core.data

import appcup.uom.polaris.features.conversational_ai.domain.Value
import kotlin.collections.map
import kotlin.collections.mapValues

fun Value.toPipecatValue(): ai.pipecat.client.types.Value {
    return when (this) {
        is Value.Bool -> ai.pipecat.client.types.Value.Bool(this.value)
        is Value.Number -> ai.pipecat.client.types.Value.Number(this.value)
        is Value.Str -> ai.pipecat.client.types.Value.Str(this.value)
        Value.Null -> ai.pipecat.client.types.Value.Null
        is Value.Array -> ai.pipecat.client.types.Value.Array(this.value.map { it.toPipecatValue() })
        is Value.Object -> ai.pipecat.client.types.Value.Object(this.value.mapValues { it.value.toPipecatValue() })
    }
}

fun ai.pipecat.client.types.Value.toDomainValue(): Value {
    return when (this) {
        is ai.pipecat.client.types.Value.Bool -> Value.Bool(this.value)
        is ai.pipecat.client.types.Value.Number -> Value.Number(this.value)
        is ai.pipecat.client.types.Value.Str -> Value.Str(this.value)
        ai.pipecat.client.types.Value.Null -> Value.Null
        is ai.pipecat.client.types.Value.Array -> Value.Array(this.value.map { it.toDomainValue() })
        is ai.pipecat.client.types.Value.Object -> Value.Object(this.value.mapValues { it.value.toDomainValue() })
    }
}