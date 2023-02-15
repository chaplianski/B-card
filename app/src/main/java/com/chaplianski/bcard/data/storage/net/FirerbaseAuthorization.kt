package com.chaplianski.bcard.data.storage.net

//class FirebaseAuthorization @Inject constructor(){
//
//
//    fun checkLoginPassword(email: String, password: String) : MutableLiveData<String>{
//        var message = ""
//        initFirebase()
//        val messageResult = MutableLiveData<String>()
//
//        AUTH.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful){
//                    messageResult.postValue("OK")
//                }
//
//            }.addOnFailureListener { exception ->
//                message = exception.message.toString()
//                messageResult.value = message
//            }
//        return messageResult
//    }
//
//
//
//
////    fun checkLoginPassword(email: String, password: String, returnResponse: (String) -> Unit){
////        var message = ""
////        initFirebase()
////        val messageResult = MutableLiveData<String>()
////
////
////        AUTH.signInWithEmailAndPassword(email, password)
////            .addOnCompleteListener { task ->
////                if (task.isSuccessful){
////                    returnResponse("OK")
////                    Log.d("MyLog", "message = ${task.result}")
////                }
////
////            }.addOnFailureListener { exception ->
////                message = exception.message.toString()
////            }
////        Log.d("MyLog", "message = $message")
////    }
//
//    private fun returnResponse(message: String): String {
//        return message
//    }
//
//
//
//    fun regUser(email: String, password: String): String{
//        var message = ""
//        initFirebase()
//        AUTH.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if(task.isSuccessful) {
//                    message = "Ok"
//                }
//            }.addOnFailureListener { exception ->
//                message = exception.message.toString()
//            }
//        return message
//    }
//
//}