package com.app.sambaaccesssmb.ui

import javax.inject.Inject

class FileCursorUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    operator fun invoke() = fileRepository.getFileCursor()
}
