package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.app.sambaaccesssmb.ui.design.SmbTheme
import com.app.sambaaccesssmb.ui.design.SmbTypography
import com.app.sambaaccesssmb.ui.design.ThemePreviews

@Composable
fun TextPlate(modifier: Modifier = Modifier, text: String = "Sample Text", style: TextStyle, color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color,
    )
}

@Composable
fun DisplayLargeText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    TextPlate(
        modifier = modifier,
        text = text,
        style = SmbTypography.displayLarge,
        color = color,
    )
}

@Composable
fun DisplayMediumText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.displayMedium,
        color = color,

    )
}

@Composable
fun DisplaySmallText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.displaySmall,
        color = color,

    )
}

@Composable
fun HeadlineLargeText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.headlineLarge,
        color = color,

    )
}

@Composable
fun HeadlineMediumText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.headlineMedium,
        color = color,

    )
}

@Composable
fun HeadlineSmallText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.headlineSmall,
        color = color,

    )
}

@Composable
fun TitleLargeText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.titleLarge,
        color = color,

    )
}

@Composable
fun TitleMediumText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.titleMedium,
        color = color,

    )
}

@Composable
fun TitleSmallText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.titleSmall,
        color = color,

    )
}

@Composable
fun BodyLargeText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.bodyLarge,
        color = color,

    )
}

@Composable
fun BodyMediumText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.bodyMedium,
        color = color,

    )
}

@Composable
fun BodySmallText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.bodySmall,
        color = color,

    )
}

@Composable
fun LabelLargeText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.labelLarge,
        color = color,

    )
}

@Composable
fun LabelMediumText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.labelMedium,
        color = color,

    )
}

@Composable
fun LabelSmallText(modifier: Modifier = Modifier, text: String = "Sample Text", color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        modifier = modifier,
        text = text,
        style = SmbTypography.labelSmall,
        color = color,

    )
}

@ThemePreviews
@Preview("Text plate previews")
@Composable
fun TextPlatePreview() {
    SmbTheme {
        Column {
            DisplayLargeText()
            DisplayMediumText()
            DisplaySmallText()
            HeadlineLargeText()
            HeadlineMediumText()
            HeadlineSmallText()
            TitleLargeText()
            TitleMediumText()
            TitleSmallText()
            LabelLargeText()
            LabelMediumText()
            LabelSmallText()
        }
    }
}
