package com.twincom.chatgpt

import com.twincom.chatgpt.network.HttpClient
import com.twincom.chatgpt.requestbody.RequestBody
import com.twincom.chatgpt.utils.Cons.CONTENT_TYPE
import com.twincom.chatgpt.utils.Cons.PREFERENCES_TOKEN
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainPresenter(private val view: MainContract.View): MainContract.Presenter {
    private val mCompositeDisposable: CompositeDisposable?
    init {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun submitOpenAi(requestBody: RequestBody) {
        view.showLoading(true)
        val disposable = HttpClient.getInstance().getApi()!!.submitOpenAi(CONTENT_TYPE, PREFERENCES_TOKEN, requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when(it.isSuccessful) {
                    true -> {
                        it.body()?.let { response -> view.submitOpenAiSuccess(response) }
                        view.showLoading(false)
                    }

                    false -> {
                        view.showLoading(false)
                    }
                }
            }, {
                view.showLoading(false)
            })
        mCompositeDisposable?.add(disposable)
    }

    override fun subscribe() {}

    override fun unSubscribe() {
        mCompositeDisposable?.clear()
    }
}