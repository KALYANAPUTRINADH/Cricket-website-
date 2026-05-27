package com.example.data.repository

import com.example.data.database.JournalDao
import com.example.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {
    val allEntries: Flow<List<JournalEntry>> = journalDao.getAllEntries()

    suspend fun insert(entry: JournalEntry) {
        journalDao.insertEntry(entry)
    }

    suspend fun delete(entry: JournalEntry) {
        journalDao.deleteEntry(entry)
    }

    suspend fun deleteById(id: Int) {
        journalDao.deleteById(id)
    }
}
