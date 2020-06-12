package io.openfuture.chain.smartcontract.scaffold

import javax.validation.constraints.NotBlank

data class SaveOpenScaffoldRequest(
        @field:NotBlank val address: String,
        @field:NotBlank val webHook: String
)
