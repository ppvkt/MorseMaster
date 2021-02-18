package ru.hadron.morsemaster.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

import dagger.hilt.android.qualifiers.ApplicationContext

import ru.hadron.morsemaster.db.MorseDatabase
import ru.hadron.morsemaster.util.Constants.MORSE_DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMorseDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        MorseDatabase::class.java,
        MORSE_DATABASE_NAME
    ).build()


    @Singleton
    @Provides
    fun provideStorageDao(db: MorseDatabase) = db.getStorageDao()
}