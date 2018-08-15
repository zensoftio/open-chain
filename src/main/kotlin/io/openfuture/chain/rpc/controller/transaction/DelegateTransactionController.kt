package io.openfuture.chain.rpc.controller.transaction

import io.openfuture.chain.core.service.DelegateTransactionService
import io.openfuture.chain.rpc.domain.transaction.request.DelegateTransactionRequest
import io.openfuture.chain.rpc.domain.transaction.response.DelegateTransactionResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/rpc/transactions/delegates")
class DelegateTransactionController(
    private val transactionService: DelegateTransactionService
) {

    @PostMapping
    fun add(@Valid @RequestBody request: DelegateTransactionRequest): DelegateTransactionResponse {
        val tx = transactionService.add(request)
        return DelegateTransactionResponse(tx)
    }

}

