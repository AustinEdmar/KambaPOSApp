package com.austin.kambapos.data.api


object CoreUrl {

   //const val PROTOCOL = "https"
   const val PROTOCOL = "http://"

    const val HOST = "192.168.214.166" // sunmi 925

   const val PORT = "8080"
   const val PUSHER_PORT = 8081
   const val PUSHER_APIKEY = "qqmsdqbnqvuhmij41258"
   const val API_PATH = "api/"


   val BASE_URL = "$PROTOCOL$HOST:$PORT/$API_PATH"

   val SERVER_URL = "$PROTOCOL$HOST:$PORT"
   val STORAGE_URL = "$SERVER_URL/storage/"
}
