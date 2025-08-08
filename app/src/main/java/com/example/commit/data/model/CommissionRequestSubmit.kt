package com.example.commit.data.model

data class CommissionRequestSubmit(
    val form_answers: Map<String, Any>,
    val image_urls: List<String> = emptyList()
) 