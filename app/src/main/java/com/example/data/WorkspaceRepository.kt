package com.example.data

import kotlinx.coroutines.flow.Flow

class WorkspaceRepository(private val workspaceDao: WorkspaceDao) {
    val allFiles: Flow<List<WorkspaceFile>> = workspaceDao.getAllFilesFlow()

    suspend fun getFileById(id: Int): WorkspaceFile? {
        return workspaceDao.getFileById(id)
    }

    suspend fun insertFile(file: WorkspaceFile): Long {
        return workspaceDao.insertFile(file)
    }

    suspend fun updateFile(file: WorkspaceFile) {
        workspaceDao.updateFile(file)
    }

    suspend fun deleteFileById(id: Int) {
        workspaceDao.deleteFileById(id)
    }

    suspend fun deleteAllFiles() {
        workspaceDao.deleteAllFiles()
    }
}
