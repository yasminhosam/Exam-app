package com.example.onlineexaminationsystem.ui.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineexaminationsystem.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamScreen(
    viewModel: CreateExamViewModel = hiltViewModel(),
    onExamCreated: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is CreateExamEvent.ExamCreated) onExamCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Exam", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(onClick = viewModel::onSaveExam) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            item {
                ExamDetailsCard(
                    title = state.title,
                    durationInput = state.durationInput,
                    passPercentageInput = state.passPercentageInput,
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    titleError = state.titleError,
                    durationError = state.durationError,
                    passPercentageError = state.passPercentageError,
                    categoryError = state.categoryError,
                    onTitleChange = viewModel::onTitleChange,
                    onDurationChange = viewModel::onDurationChange,
                    onPassPercentageChange = viewModel::onPassPercentageChange,
                    onCategorySelected = viewModel::onCategorySelected
                )
            }

            state.globalError?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Questions (${state.questions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalButton(
                        onClick = viewModel::addQuestion,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }
            }

            itemsIndexed(state.questions, key = { _, q -> q.id }) { index, question ->
                QuestionCard(
                    number = index + 1,
                    question = question,
                    onTextChange = { viewModel.onQuestionTextChange(question.id, it) },
                    onOptionChange = { optIdx, value -> viewModel.onOptionChange(question.id, optIdx, value) },
                    onCorrectOptionChange = { viewModel.onCorrectOptionChange(question.id, it) },
                    onMarkChange = { viewModel.onMarkChange(question.id, it) },
                    onRemove = { viewModel.removeQuestion(question.id) }
                )
            }


            if (state.questions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No questions yet. Tap \"Add\" to begin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamDetailsCard(
    title: String,
    durationInput: String,
    passPercentageInput: String,
    categories: List<Category>,
    selectedCategory: Category?,
    titleError: String?,
    durationError: String?,
    passPercentageError: String?,
    categoryError: String?,
    onTitleChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onPassPercentageChange: (String) -> Unit,
    onCategorySelected: (Category) -> Unit
) {
    var categoryExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Exam Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Exam Title *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Duration + Pass percentage in one row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = durationInput,
                    onValueChange = onDurationChange,
                    label = { Text("Duration (min) *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    isError = durationError != null,
                    supportingText = { durationError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )
                OutlinedTextField(
                    value = passPercentageInput,
                    onValueChange = onPassPercentageChange,
                    label = { Text("Pass % *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    isError = passPercentageError != null,
                    supportingText = { passPercentageError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )
            }

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select a category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    isError = categoryError != null,
                    supportingText = { categoryError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                onCategorySelected(category)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}



private val optionLabels = listOf("A", "B", "C", "D")

@Composable
private fun QuestionCard(
    number: Int,
    question: QuestionDraft,
    onTextChange: (String) -> Unit,
    onOptionChange: (Int, String) -> Unit,
    onCorrectOptionChange: (Int) -> Unit,
    onMarkChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            "$number",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Question $number",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }

            // Question text
            OutlinedTextField(
                value = question.text,
                onValueChange = onTextChange,
                label = { Text("Question text *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                maxLines = 4,
                isError = question.textError != null,
                supportingText = { question.textError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Options — matches Question.options: List<String> and Question.correctAnswer: Int
            Text(
                text = "Options  •  select the correct answer",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            question.options.forEachIndexed { idx, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = question.correctOptionIndex == idx,
                        onClick = { onCorrectOptionChange(idx) }
                    )
                    OutlinedTextField(
                        value = option,
                        onValueChange = { onOptionChange(idx, it) },
                        label = { Text("Option ${optionLabels[idx]}") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = if (question.correctOptionIndex == idx)
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            )
                        else OutlinedTextFieldDefaults.colors()
                    )
                }
            }

            question.optionsError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }


            OutlinedTextField(
                value = question.markInput,
                onValueChange = onMarkChange,
                label = { Text("Mark") },
                modifier = Modifier.width(120.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }
    }
}