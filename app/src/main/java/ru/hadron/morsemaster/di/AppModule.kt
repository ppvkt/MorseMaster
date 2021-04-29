package ru.hadron.morsemaster.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

import dagger.hilt.android.qualifiers.ApplicationContext

import ru.hadron.morsemaster.db.MorseDatabase
import ru.hadron.morsemaster.util.Constants.MORSE_DATABASE_NAME
import ru.hadron.morsemaster.util.Constants.SHARED_PREFERENCES_NAME
import ru.hadron.morsemaster.util.Sound
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMorseDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app,
            MorseDatabase::class.java,
            MORSE_DATABASE_NAME
        )
            .build()


    @Singleton
    @Provides
    fun provideStorageDao(db: MorseDatabase) = db.getStorageDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) = app.getSharedPreferences(
        SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }
}