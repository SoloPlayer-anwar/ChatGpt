Version Library
livedata = "2.8.1"
viewModel = "2.8.1"
navigationFragment = "2.7.7"
navigationUi = "2.7.7"
fragmentKtx = "1.7.1"
rxjavaAdapter = "2.6.2"
okhttp = "4.10.0"
rxAndroid = "2.1.0"
rxKotlin = "2.2.0"
retrofit = "2.9.0"
converterGson = "2.9.0"
androidxPreferences = "1.2.1"
multiDex = "2.0.1"
glide = "4.15.1"
swipeRefresh = "1.1.0"
lottieAnimation = "6.0.0"
midtrans = "2.0.0-SANDBOX"
imagePicker = "2.1"
safeArgs = "2.7.7"

#Library
#Live Data
lifecycle-livedata = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "livedata" }

#viewModel
view-model = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "viewModel" }

#NavigationFragmentKTX
navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigationFragment" }

#NavigationUiKtx
navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigationUi" }

#FragmentKtx
fragment-ktx = { group = "androidx.fragment", name = "fragment-ktx", version.ref = "fragmentKtx" }

#RxJavaAdapter
rx-java-adapter = { group = "com.squareup.retrofit2", name = "adapter-rxjava2", version.ref = "rxjavaAdapter"}

#Okhttp3
okhttp3 = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp"}

#RxAndroid
rx-android = { group = "io.reactivex.rxjava2", name = "rxandroid", version.ref = "rxAndroid"}

#RxKotlin
rx-kotlin = { group = "io.reactivex.rxjava2", name = "rxkotlin", version.ref = "rxKotlin"}

#Retrofit
retrofit2 = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit"}

#Converter Gson
converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "converterGson"}

#AndroidxPreferences
androidx-preferences = {group = "androidx.preference" , name = "preference-ktx", version.ref = "androidxPreferences"}

#MultiDex
multidex = {group = "androidx.multidex" , name = "multidex", version.ref = "multiDex"}

#Glide
glide = {group = "com.github.bumptech.glide" , name = "glide", version.ref = "glide"}

#Swipe Refresh
swipe-refresh = {group = "androidx.swiperefreshlayout" , name = "swiperefreshlayout", version.ref = "swipeRefresh"}

#lottie Animation
lottie-animation = {group = "com.airbnb.android" , name = "lottie", version.ref = "lottieAnimation"}

#Midtrans
midtrans = {group = "com.midtrans" , name = "uikit", version.ref = "midtrans"}

#Image Picker
image-picker = {group = "com.github.dhaval2404" , name = "imagepicker", version.ref = "imagePicker"}

Plugins
safe-argument = { id = "androidx.navigation.safeargs.kotlin", version.ref = "safeArgs"}
alias(libs.plugins.safe.argument)
id("kotlin-parcelize")

#BaseApp
class BaseApp: MultiDexApplication() {

    companion object {
        lateinit var instance: BaseApp

        fun getApp(): BaseApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    fun setToken(token: String) {
        getPreferences().edit().putString(PREFERENCES_TOKEN, token).apply()
        HttpClient.getInstance().buildRetrofitClient(token)
    }

    fun getToken(): String? {
        return getPreferences().getString(PREFERENCES_TOKEN, null)
    }

    fun setUser(user: String) {
        getPreferences().edit().putString(PREFERENCES_USER, user).apply()
    }

    fun getUser(): String? {
        return getPreferences().getString(PREFERENCES_USER, null)
    }

    fun clearToken() {
        getPreferences().edit().remove(PREFERENCES_TOKEN).apply()
        HttpClient.getInstance().buildRetrofitClient("")
    }

}

#HttpClient
class HttpClient {
    private var client: Retrofit? = null
    private var endpoint: Endpoint? = null

    companion object {
        private val mInstance: HttpClient = HttpClient()

        @Synchronized
        fun getInstance(): HttpClient {
            return mInstance
        }
    }

    init {
        buildRetrofitClient()
    }

    fun getApi(): Endpoint? {
        if (endpoint == null) {
            endpoint = client!!.create(Endpoint::class.java)
        }
        return endpoint
    }

    private fun buildRetrofitClient() {
        val token = BaseApp.getApp().getToken()
        buildRetrofitClient(token)
    }

    fun buildRetrofitClient(token: String?) {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        if (token != null) {
            builder.addInterceptor("Authorization".getInterceptorWithHeader("Bearer $token"))
        }

        val okHttpClient = builder.build()
        client =
            Retrofit.Builder()
                .baseUrl(BASE_URL + "api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(Helpers.getDefaultGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build()
        endpoint = null
    }

    private fun String.getInterceptorWithHeader(
        headerValue: String,
    ): Interceptor {
        val header = HashMap<String, String>()
        header[this] = headerValue
        return getInterceptorWithHeader(header)
    }

    private fun getInterceptorWithHeader(headers: Map<String, String>): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            for ((key, value) in headers) {
                builder.addHeader(key, value)
            }
            builder.method(original.method, original.body)
            chain.proceed(builder.build())
        }
    }
}

#Cons
object Cons {
    const val PREFERENCES_TOKEN = "preferences_token"
    const val PREFERENCES_USER = "preferences_user"
    const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
}

#Helpers
object Helpers {
    fun getDefaultGson(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .setDateFormat(Cons.DATE_FORMAT_SERVER)
            .registerTypeAdapter(Date::class.java, JsonDeserializer { json, _, _ ->
                val formatServer = SimpleDateFormat(Cons.DATE_FORMAT_SERVER, Locale.ENGLISH)
                formatServer.timeZone = TimeZone.getTimeZone("UTC")
                formatServer.parse(json.asString)
            })
            .registerTypeAdapter(Date::class.java, JsonSerializer<Date> { src, _, _ ->
                val format = SimpleDateFormat(Cons.DATE_FORMAT_SERVER, Locale.ENGLISH)
                format.timeZone = TimeZone.getTimeZone("UTC")
                if (src != null) {
                    JsonPrimitive(format.format(src))
                } else {
                    null
                }
            })
            .create()
    }

    fun TextView.formatPrice(value: String) {
        this.text = getCurrencyIdr(java.lang.Double.parseDouble(value))
    }

    @JvmStatic
    fun getCurrencyIdr(price: Double): String {
        val format = DecimalFormat("#,###,###")
        return "Rp " + format.format(price).replace(",".toRegex(), ".")
    }
}
