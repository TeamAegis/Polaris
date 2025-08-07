package appcup.uom.polaris.features.polaris.presentation.waypoint_selector.components

import android.text.Spannable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.compose.autocomplete.data.toDistanceString
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = TextFieldState(""),
    onQueryChange: (String) -> Unit = {},
    onSearch: (AutocompletePlace?) -> Unit,
    predictions: List<AutocompletePlace>,
    onSelected: (AutocompletePlace) -> Unit = {},
    selectedPlace: AutocompletePlace? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    var expandedPlaceId by remember(predictions) { mutableStateOf<String?>(null) }

    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            shadowElevation = 12.dp,
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        onQueryChange(textFieldState.text.toString())
                    },
                    onSearch = {
                        onSearch(if (predictions.isEmpty()) null else predictions.first())
                        keyboardController?.hide()
                        expanded = false
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search...") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .verticalScroll(scrollState)
            ) {
                for (prediction in predictions) {
                    AutocompletePlaceRow(
                        autocompletePlace = prediction,
                        isSelected = prediction.placeId == selectedPlace?.placeId,
                        onPlaceSelected = {
                            keyboardController?.hide()
                            onSelected(it)
                        },
                        isExpanded = expandedPlaceId == prediction.placeId,
                        onExpandClick = {
                            keyboardController?.hide()
                            expandedPlaceId =
                                if (expandedPlaceId == prediction.placeId) null else prediction.placeId
                        },
                        primaryTextMaxLines = 2,
                        secondaryTextMaxLines = 2,
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun AutocompletePlaceRow(
    autocompletePlace: AutocompletePlace,
    isSelected: Boolean,
    onPlaceSelected: (AutocompletePlace) -> Unit,
    primaryTextMaxLines: Int,
    secondaryTextMaxLines: Int,
    isExpanded: Boolean = false,
    onExpandClick: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
            )
            .clickable { onPlaceSelected(autocompletePlace) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AutocompleteIcon(
            caption = autocompletePlace.distance?.toDistanceString()
        )
        Spacer(modifier = Modifier.width(16.dp))

        val primaryText = autocompletePlace.primaryText.toAnnotatedString(predictionsHighlightStyle)
        val secondaryText =
            autocompletePlace.secondaryText.toAnnotatedString(predictionsHighlightStyle)

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {

            Column(
                modifier = Modifier.weight(1f, true),
            ) {
                val (primaryTextLines, secondaryTextLines) = if (isExpanded) {
                    Int.MAX_VALUE to Int.MAX_VALUE
                } else {
                    primaryTextMaxLines to secondaryTextMaxLines
                }

                var primaryTextLayoutResult by remember(autocompletePlace) {
                    mutableStateOf<TextLayoutResult?>(
                        null
                    )
                }
                var secondaryTextLayoutResult by remember(autocompletePlace) {
                    mutableStateOf<TextLayoutResult?>(
                        null
                    )
                }

                // This indicates whether the text is currently clipped.
                val isClipped by remember(autocompletePlace) {
                    derivedStateOf {
                        primaryTextLayoutResult?.hasVisualOverflow == true ||
                                secondaryTextLayoutResult?.hasVisualOverflow == true
                    }
                }

                // This remembers if the text can be clipped even if it is not currently clipped.
                // It acts like a latch in that once it becomes true, it will stay true.
                var isClippable by remember(autocompletePlace) { mutableStateOf(false) }
                LaunchedEffect(autocompletePlace) {
                    snapshotFlow { isClipped }
                        .filter { it }
                        .collect { isClippable = true }
                }

                val targetSizeDp: Dp = 44.dp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isClippable) {
                                Modifier.clickable { onExpandClick(autocompletePlace.placeId) }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, true)
                            .animateContentSize()
                            .then(
                                if (isClippable) {
                                    Modifier.clickable { onPlaceSelected(autocompletePlace) }
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = primaryTextLines,
                            onTextLayout = { primaryTextLayoutResult = it },
                            overflow = TextOverflow.Ellipsis,
                            text = primaryText,
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            onTextLayout = { secondaryTextLayoutResult = it },
                            maxLines = secondaryTextLines,
                            overflow = TextOverflow.Ellipsis,
                            text = secondaryText,
                        )
                    }

                    if (isClippable) {
                        Icon(
                            imageVector = if (isClipped) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            modifier = Modifier
                                .size(targetSizeDp)
                                .padding(top = 16.dp)
                                .clickable { onExpandClick(autocompletePlace.placeId) }
                                .align(Alignment.Bottom)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AutocompleteIcon(caption: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier =
                Modifier
                    .background(color = Color.LightGray.copy(0.5f), shape = CircleShape)
                    .size(40.dp)
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = Icons.Outlined.Place,
                contentDescription = "Place",
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        caption?.let { distanceString ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = distanceString,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

internal val predictionsHighlightStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold)
internal fun Spannable.toAnnotatedString(spanStyle: SpanStyle?): AnnotatedString {
    return buildAnnotatedString {
        if (spanStyle == null) {
            append(this@toAnnotatedString.toString())
        } else {
            var last = 0
            for (span in getSpans(0, this@toAnnotatedString.length, Any::class.java)) {
                val start = this@toAnnotatedString.getSpanStart(span)
                val end = this@toAnnotatedString.getSpanEnd(span)
                if (last < start) {
                    append(this@toAnnotatedString.substring(last, start))
                }
                withStyle(spanStyle) {
                    append(this@toAnnotatedString.substring(start, end))
                }
                last = end
            }
            if (last < this@toAnnotatedString.length) {
                append(this@toAnnotatedString.substring(last))
            }
        }
    }
}