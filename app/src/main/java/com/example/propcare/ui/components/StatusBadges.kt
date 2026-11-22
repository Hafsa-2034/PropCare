package com.example.propcare.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.propcare.domain.model.ComplaintStatus
import com.example.propcare.domain.model.PriorityLevel

@Composable
fun PriorityBadge(priority: PriorityLevel, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (priority) {
        PriorityLevel.LOW -> colors.secondaryContainer to colors.onSecondaryContainer
        PriorityLevel.MEDIUM -> colors.tertiaryContainer to colors.onTertiaryContainer
        PriorityLevel.HIGH -> colors.errorContainer to colors.onErrorContainer
        PriorityLevel.URGENT -> colors.error to colors.onError
    }
    BadgeChip("${priority.label} priority", container, content, modifier)
}

@Composable
fun StatusBadge(status: ComplaintStatus, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (status) {
        ComplaintStatus.OPEN -> colors.surfaceVariant to colors.onSurfaceVariant
        ComplaintStatus.ASSIGNED -> colors.secondaryContainer to colors.onSecondaryContainer
        ComplaintStatus.IN_PROGRESS -> colors.tertiaryContainer to colors.onTertiaryContainer
        ComplaintStatus.RESOLVED -> colors.primaryContainer to colors.onPrimaryContainer
    }
    BadgeChip(status.label, container, content, modifier)
}

@Composable
private fun BadgeChip(label: String, container: Color, content: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = container,
        contentColor = content
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

