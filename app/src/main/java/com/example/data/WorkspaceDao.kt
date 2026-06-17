package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspace_files ORDER BY timestamp DESC")
    fun getAllFilesFlow(): Flow<List<WorkspaceFile>>

    @Query("SELECT * FROM workspace_files WHERE id = :id")
    suspend fun getFileById(id: Int): WorkspaceFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: WorkspaceFile): Long

    @Update
    suspend fun updateFile(file: WorkspaceFile)

    @Query("DELETE FROM workspace_files WHERE id = :id")
    suspend fun deleteFileById(id: Int)

    @Query("DELETE FROM workspace_files")
    suspend fun deleteAllFiles()
}
