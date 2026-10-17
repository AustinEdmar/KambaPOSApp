package com.austin.kambapos.data.api


object CoreUrl {

   //const val PROTOCOL = "https"
   const val PROTOCOL = "http://"

    const val HOST = "172.20.10.2" // iphone 172.20.10.2

   const val PORT = "8080"
   const val PUSHER_PORT = 8081
   const val PUSHER_APIKEY = "qqmsdqbnqvuhmij41258"
   const val API_PATH = "api/"



   val BASE_URL = "$PROTOCOL$HOST:$PORT/$API_PATH"

    // url

   val SERVER_URL = "$PROTOCOL$HOST:$PORT"
    //url image
   val STORAGE_URL = "$SERVER_URL/storage/"
}
