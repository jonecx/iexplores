package com.app.sambaaccesssmb.ui

import javax.inject.Inject

class NetShareUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    operator fun invoke() = fileRepository.getNetShares()
}
