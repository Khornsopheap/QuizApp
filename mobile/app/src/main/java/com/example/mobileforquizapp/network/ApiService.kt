package com.example.mobileforquizapp.network

import com.example.mobileforquizapp.login.model.LoginResponse
import com.example.mobileforquizapp.login.model.User
import com.example.mobileforquizapp.quiz.model.Question
import com.example.mobileforquizapp.quiz.model.Quiz
import com.example.mobileforquizapp.quiz.model.QuizSubmission
import com.example.mobileforquizapp.quiz.model.ResultResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body user: User): Call<LoginResponse>

    @GET("quizzes")
    fun getQuizzes(@Header("Authorization") token: String): Call<List<Quiz>>


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

    @DELETE("questions/{id}")
    fun deleteQuestion(
        @Header("Authorization") token: String,
        @Path("id") questionId: Long
    ): Call<Void>


    @DELETE("questions/{id}")
    fun deleteQuestion(
        @Header("Authorization") token: String,
        @Path("id") questionId: Long?
    ): Call<Void>

}
