package com.example.customerchallenge.presentation.feature.customer.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.customerchallenge.R
import com.example.customerchallenge.domain.model.Customer
import com.example.customerchallenge.ui.theme.CustomerChallengeTheme

@Composable
fun CustomerItem(
    customer: Customer,
    onProfileClick: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageLoadedSuccessfully by remember(customer.profileImage) {
        mutableStateOf(false)
    }

    val hasProfileLink = !customer.profileLink.isNullOrBlank()
    val phone = customer.phone?.takeIf { it.isNotBlank() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val fontScale = LocalDensity.current.fontScale
            val useCompactLayout = maxWidth < 360.dp || fontScale >= 1.3f

            CustomerItemContent(
                customer = customer,
                phone = phone,
                hasProfileLink = hasProfileLink,
                useCompactLayout = useCompactLayout,
                imageLoadedSuccessfully = imageLoadedSuccessfully,
                onImageLoading = { imageLoadedSuccessfully = false },
                onImageSuccess = { imageLoadedSuccessfully = true },
                onImageError = { imageLoadedSuccessfully = false },
                onProfileClick = onProfileClick,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
private fun CustomerItemContent(
    customer: Customer,
    phone: String?,
    hasProfileLink: Boolean,
    useCompactLayout: Boolean,
    imageLoadedSuccessfully: Boolean,
    onImageLoading: () -> Unit,
    onImageSuccess: () -> Unit,
    onImageError: () -> Unit,
    onProfileClick: () -> Unit,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(if (useCompactLayout) 4.dp else 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = customer.profileImage
                    ?.takeIf { it.isNotBlank() },
                contentDescription = "Profile image of ${customer.name}",
                modifier = Modifier
                    .size(if (useCompactLayout) 56.dp else 72.dp)
                    .clip(CircleShape)
                    .clickable(
                        enabled = imageLoadedSuccessfully,
                        onClick = onImageClick
                    ),
                contentScale = ContentScale.Crop,
                onLoading = { onImageLoading() },
                onSuccess = { onImageSuccess() },
                onError = { onImageError() },
                error = painterResource(id = R.drawable.default_image),
                fallback = painterResource(id = R.drawable.default_image)
            )

            Spacer(
                modifier = Modifier.width(
                    if (useCompactLayout) 12.dp else 16.dp
                )
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (useCompactLayout) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = customer.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (useCompactLayout) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomerStatus(status = customer.status)

                    Text(
                        text = "ID ${customer.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                phone?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (hasProfileLink && !useCompactLayout) {
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onProfileClick
                ) {
                    Text(
                        text = "Open profile",
                        maxLines = 1
                    )
                }
            }
        }

        if (hasProfileLink && useCompactLayout) {
            TextButton(
                onClick = onProfileClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Open profile")
            }
        }
    }
}

@Composable
private fun CustomerStatus(
    status: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1
        )
    }
}

val previewCustomer = Customer(
    id = "123456",
    name = "Maria Fernanda de Oliveira",
    status = "Active",
    email = "maria.fernanda.oliveira@example.com",
    phone = "+55 91 99999-9999",
    profileImage = null,
    profileLink = "https://github.com/maria"
)

private val previewCustomerWithoutOptionalFields = Customer(
    id = "987654",
    name = "João Silva",
    status = "Inactive",
    email = "joao.silva@example.com",
    phone = null,
    profileImage = null,
    profileLink = null
)

@Preview(
    name = "Compact phone - 320dp",
    widthDp = 320,
    heightDp = 640,
    showBackground = true
)
@Preview(
    name = "Large phone - 430dp",
    widthDp = 430,
    heightDp = 900,
    showBackground = true
)
@Preview(
    name = "Tablet - 840dp",
    widthDp = 840,
    heightDp = 1180,
    showBackground = true
)
@Composable
private fun CustomerItemResponsivePreview() {
    CustomerItemPreviewContainer {
        CustomerItem(
            customer = previewCustomer,
            onProfileClick = {},
            onImageClick = {}
        )
    }
}

@Preview(
    name = "Large font",
    widthDp = 360,
    heightDp = 720,
    fontScale = 1.5f,
    showBackground = true
)
@Composable
private fun CustomerItemLargeFontPreview() {
    CustomerItemPreviewContainer {
        CustomerItem(
            customer = previewCustomer,
            onProfileClick = {},
            onImageClick = {}
        )
    }
}

@Preview(
    name = "Dark theme",
    widthDp = 430,
    heightDp = 900,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun CustomerItemDarkPreview() {
    CustomerChallengeTheme(
        darkTheme = true
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            CustomerItem(
                customer = previewCustomer,
                onProfileClick = {},
                onImageClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(
    name = "Without optional fields",
    widthDp = 360,
    heightDp = 720,
    showBackground = true
)
@Composable
private fun CustomerItemWithoutOptionalFieldsPreview() {
    CustomerItemPreviewContainer {
        CustomerItem(
            customer = previewCustomerWithoutOptionalFields,
            onProfileClick = {},
            onImageClick = {}
        )
    }
}

@Composable
private fun CustomerItemPreviewContainer(
    content: @Composable () -> Unit
) {
    CustomerChallengeTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}