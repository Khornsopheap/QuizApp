    package com.example.mobileforquizapp.network

    import com.example.mobileforquizapp.login.model.LoginResponse
    import com.example.mobileforquizapp.login.model.RegisterRequest
    import com.example.mobileforquizapp.login.model.User
    import com.example.mobileforquizapp.quiz.model.Question
    import com.example.mobileforquizapp.quiz.model.Quiz
    import com.example.mobileforquizapp.quiz.model.QuizSubmission
    import com.example.mobileforquizapp.quiz.model.ResultResponse
    import retrofit2.Call
    import retrofit2.http.*

    interface ApiService {

        @POST("login")
        fun login(
            @Body user: User
        ): Call<LoginResponse>

        @GET("quizzes")
        fun getQuizzes(
            @Header("Authorization") token: String
        ): Call<List<Quiz>>

        @POST("quizzes/{id}/submit")
        fun submitQuiz(
            @Header("Authorization") token: String,
            @Path("id") quizId: Long,
            @Body submission: QuizSubmission
        ): Call<ResultResponse>

        @POST("admin/quizzes/add")
        fun addQuiz(
            @Header("Authorization") token: String,
            @Body question: Question
        ): Call<Question>

        @POST("quizzes")
        fun createQuiz(
            @Header("Authorization") token: String,
            @Body quiz: Quiz
        ): Call<Quiz>

        @PUT("quizzes/{id}")
        fun updateQuiz(
            @Header("Authorization") token: String,
            @Path("id") quizId: Long,
            @Body quiz: Quiz
        ): Call<Quiz>


        @DELETE("quizzes/{id}")
        fun deleteQuiz(
            @Header("Authorization") token: String,
            @Path("id") quizId: Long
        ): Call<Void>

        @GET("quizzes/{id}/questions")
        fun getQuestionsByQuizId(
            @Header("Authorization") token: String,
            @Path("id") quizId: Long
        ): Call<List<Question>>

        @PUT("questions/{id}")
        fun updateQuestion(
            @Header("Authorization") token: String,
            @Path("id") questionId: Long,
            @Body question: Question
        ): Call<Question>

        @POST("questions/quiz/{quizId}")
        fun createQuestion(
            @Header("Authorization") token: String,
            @Path("quizId") quizId: Long,
            @Body question: Question
        ): Call<Question>

        @DELETE("questions/{id}")
        fun deleteQuestion(
            @Header("Authorization") token: String,
            @Path("id") questionId: Long
        ): Call<Void>

        @POST("session/create/{quizId}")
        fun createSession(
            @Header("Authorization") token: String,
            @Path("quizId") quizId: Long
        ): Call<Map<String, String>>

        @POST("session/join/{roomCode}")
        fun joinSession(
            @Header("Authorization") token: String,
            @Path("roomCode") roomCode: String
        ): Call<Map<String, Any>>

        @POST("session/submit/{roomCode}")
        fun submitScore(
            @Header("Authorization") token: String,
            @Path("roomCode") roomCode: String,
            @Body body: Map<String, Int>
        ): Call<String>

        @GET("session/leaderboard/{roomCode}")
        fun getLeaderboard(
            @Header("Authorization") token: String,
            @Path("roomCode") roomCode: String
        ): Call<List<Map<String, Any>>>

        @POST("register")
        fun register(
            @Body request: RegisterRequest
        ): Call<String>
    }