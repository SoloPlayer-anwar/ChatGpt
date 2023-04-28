package com.twincom.chatgpt

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.twincom.chatgpt.adapter.AdapterOpenAi
import com.twincom.chatgpt.databinding.ActivityMainBinding
import com.twincom.chatgpt.requestbody.RequestBody
import com.twincom.chatgpt.response.OpenAiResponse

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var binding: ActivityMainBinding
    private var loadingDialog: Dialog? = null
    private lateinit var presenter: MainContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoading()
        clearText()
        presenter = MainPresenter(view = this)

        binding.sendMessage.setOnClickListener {
            val messageValue = binding.etMessage.text.toString()

            if (messageValue.isEmpty()) {
                binding.TextInputLayoutMessage.error = "Tidak boleh kosong"
                binding.TextInputLayoutMessage.requestFocus()
            }

            else {
                presenter.submitOpenAi(
                    RequestBody(
                        model = "text-davinci-003",
                        prompt = messageValue,
                        max_tokens = 4000,
                        temperature = 1
                    )
                )
            }
        }

    }

    private fun clearText() {
        binding.etMessage.text?.clear()
    }

    @SuppressLint("InflateParams")
    private fun initLoading() {
        loadingDialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.loading_ai, null)

        loadingDialog?.let {
            it.setContentView(dialogView)
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
            it.window?.setBackgroundDrawableResource(R.color.tsp)
        }
    }

    override fun submitOpenAiSuccess(openAiResponse: OpenAiResponse) {
        clearText()
        val adapterOpenAi = AdapterOpenAi(openAiResponse.choices)

        binding.LinearLayoutVis.visibility = if (openAiResponse.choices.isEmpty()) View.VISIBLE else View.GONE

        binding.rvMessageValue.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adapterOpenAi
        }
    }

    override fun showLoading(loading: Boolean) {
        when(loading) {
            true -> loadingDialog?.show()
            false -> loadingDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        if (::presenter.isInitialized) {
            presenter.unSubscribe()
        }
        super.onDestroy()
    }
}