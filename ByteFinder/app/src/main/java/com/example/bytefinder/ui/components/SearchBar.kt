package com.example.bytefinder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytefinder.ui.theme.*

/**
 * ClaySearchBar — Barra de pesquisa com estilo Claymorphism.
 * Campo de texto com placeholder, ícone, e botão de limpar.
 */
@Composable
fun ClaySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Procurar pizza, sushi, burger..."
) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ClayBeige,
        cornerRadius = 50.dp,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Pesquisar",
                tint = ClayTextLight
            )

            Spacer(Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        placeholder,
                        color = ClayTextLight,
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = ClayTextDark
                    )
                )
            }

            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpar pesquisa",
                        tint = ClayTextMedium
                    )
                }
            }
        }
    }
}
