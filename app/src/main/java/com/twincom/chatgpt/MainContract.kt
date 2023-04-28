package com.twincom.chatgpt

import com.twincom.chatgpt.base.BasePresenter
import com.twincom.chatgpt.base.BaseView
import com.twincom.chatgpt.requestbody.RequestBody
import com.twincom.chatgpt.response.OpenAiResponse

interface MainContract {
    interface View: BaseView {
        fun submitOpenAiSuccess(openAiResponse: OpenAiResponse)
    }

    interface Presenter: BasePresenter {
        fun submitOpenAi(requestBody: RequestBody)
    }
}