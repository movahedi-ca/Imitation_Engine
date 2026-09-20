package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    ChatSessionEntity::class,
    ChatMessageEntity::class,
    UserVectorEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class TuringDatabase : RoomDatabase() {
  abstract fun turingDao(): TuringDao

  companion object {
    @Volatile
    private var INSTANCE: TuringDatabase? = null

    fun getDatabase(context: Context): TuringDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TuringDatabase::class.java,
          "turing_vault.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
