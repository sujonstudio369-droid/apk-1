package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.Formatters
import com.example.data.Photo
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.MuteColor
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor
import com.example.ui.theme.tvFocusRing

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    allPhotos: List<Photo>,
    onPhotoSelected: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    var isFocused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(d.rem(0.9f))
    val bgColor = if (isFocused) S2Color else S1Color
    val borderColor = if (isFocused) MuteColor else LineColor

    val suggestions = remember(query, allPhotos) {
        if (query.trim().isEmpty()) {
            emptyList()
        } else {
            val qNorm = Formatters.normalize(query.trim())
            val tokens = qNorm.split("\\s+".toRegex()).filter { it.isNotEmpty() }
            allPhotos.filter { p ->
                val nameNorm = Formatters.normalize(p.name)
                tokens.all { nameNorm.contains(it) }
            }.take(8)
        }
    }

    val inputWidth by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isFocused || query.isNotEmpty()) d.rem(12f) else d.rem(9.5f),
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 200),
        label = "search_field_width"
    )

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(d.rem(2.5f))
                .tvFocusRing(isFocused, d.rem(0.9f))
                .clip(shape)
                .background(bgColor)
                .border(BorderStroke(d.px(1f), borderColor), shape)
                .padding(horizontal = d.rem(0.8f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.rem(0.5f))
        ) {
            AppIcon(
                icon = AppIconType.SEARCH,
                tint = if (isFocused) FgColor else MuteColor,
                size = d.rem(1.05f)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = FgColor,
                    fontSize = d.sp(0.875f)
                ),
                cursorBrush = SolidColor(FgColor),
                modifier = Modifier
                    .width(inputWidth)
                    .onFocusChanged { isFocused = it.isFocused },
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search…",
                            color = MuteColor,
                            fontSize = d.sp(0.875f)
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(d.rem(1.5f))
                        .clip(RoundedCornerShape(99.dp))
                        .clickable { onQueryChange("") },
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        icon = AppIconType.X,
                        tint = MuteColor,
                        size = d.rem(0.85f)
                    )
                }
            }
        }

        // Suggestions Dropdown
        if (isFocused && suggestions.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
                offset = androidx.compose.ui.unit.IntOffset(0, with(LocalDimens.current.density) { d.rem(2.7f).roundToPx() }),
                onDismissRequest = { /* dismiss */ }
            ) {
                val popShape = RoundedCornerShape(d.rem(1.1f))
                Column(
                    modifier = Modifier
                        .widthIn(min = 300.dp, max = 400.dp)
                        .heightIn(max = d.rem(22f))
                        .shadow(20.dp, popShape)
                        .clip(popShape)
                        .background(S2Color)
                        .border(BorderStroke(d.px(1f), S4Color), popShape)
                        .padding(d.rem(0.35f))
                        .verticalScroll(rememberScrollState())
                ) {
                    suggestions.forEach { photo ->
                        SuggestionRow(
                            photo = photo,
                            query = query,
                            onClick = {
                                onPhotoSelected(photo)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionRow(
    photo: Photo,
    query: String,
    onClick: () -> Unit
) {
    val d = LocalDimens.current
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val rowShape = RoundedCornerShape(d.rem(0.75f))
    val bgColor = if (isFocused) S3Color else Color.Transparent

    val annotatedName = remember(photo.name, query) {
        val qTokens = Formatters.normalize(query.trim()).split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val nameNorm = Formatters.normalize(photo.name)
        buildAnnotatedString {
            var i = 0
            while (i < photo.name.length) {
                var matched = false
                for (token in qTokens) {
                    if (nameNorm.startsWith(token, i)) {
                        pushStyle(SpanStyle(color = FgColor, fontWeight = FontWeight.SemiBold))
                        append(photo.name.substring(i, i + token.length))
                        pop()
                        i += token.length
                        matched = true
                        break
                    }
                }
                if (!matched) {
                    append(photo.name[i])
                    i++
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(bgColor)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(horizontal = d.rem(0.7f), vertical = d.rem(0.55f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.7f))
    ) {
        val thumbShape = RoundedCornerShape(d.rem(0.55f))
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.uri)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(d.rem(2.25f))
                .clip(thumbShape)
                .background(S3Color)
        )

        Text(
            text = annotatedName,
            color = if (isFocused) FgColor else SubColor,
            fontSize = d.sp(0.875f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = photo.folderId.replace("_", " ").replaceFirstChar { it.uppercase() },
            color = MuteColor,
            fontSize = d.sp(0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
