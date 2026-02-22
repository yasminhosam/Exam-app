# 📝 Online Examination System

A full-featured Android exam management app built with **Jetpack Compose** and **MVVM architecture**. Supports two roles — **Admin** to create and manage exams, and **Student** to take exams, track progress, and review answers.

---

## 📱 Screenshots

<table>
  <tr>
    <td align="center"><b>Login</b></td>
    <td align="center"><b>Home</b></td>
    <td align="center"><b>Exam</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/8f604518-0602-4c30-abcc-0a66aaf5947d" width="220"/></td>
     <td><img src="https://github.com/user-attachments/assets/75e1ff32-8ffb-48c7-ad89-d2688d48aed2" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/0b0d239a-5116-4ab1-874d-0d98c85c65d2" width="220"/></td>
  </tr>
  <tr>
    <td align="center"><b>Progress</b></td>
    <td align="center"><b>Profile</b></td>
    <td align="center"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/67c2e234-4982-4d2e-a015-355e3e5363a2" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/2f2ec6ce-c949-4a6f-ae7d-8998375a243f" width="220"/></td>
    <td></td>
  </tr>
</table>

---

## ✨ Features

### 🎓 Student
- **Register & Login** — Role-based authentication with form validation (email format, password strength)
- **Browse Exams** — View available exams organized by category (Science, Math, Programming, Geography, Physics, Biology...)
- **Take Exam** — Answer multiple-choice questions with a live circular countdown timer
- **Auto Submit** — Exam auto-submits when the timer runs out
- **Results** — Instant score, grade (A/B/C/D/F), and pass/fail status after submission
- **Review Answers** — Step through each question after the exam with correct vs selected answer highlighted
- **Progress Tracking** — Full history of all submitted exams with score, grade, date and pass/fail badge

### 🛠️ Admin *(in progress)*
- Create / delete exams
- Add multiple-choice questions with custom marks
- View top students leaderboard per exam

---

## 🏗️ Architecture

```
app/
├── data/
│   ├── local/
│   │   ├── dao/          # ExamDao, StudentDao, UserDao
│   │   ├── AppDatabase   # Room database
│   │   └── Converters    # Type converters (Duration, List<String>)
│   ├── model/            # Exam, Question, User, AnswerSnapshot, SubmittedExam...
│   └── repository/       # AuthRepository, ExamRepository, StudentRepository
├── di/                   # Hilt module
└── ui/
    ├── navigation/        # NavGraph, Screen routes
    ├── screen/            # All Compose screens
    ├── viewmodel/         # ViewModel per screen
    └── theme/             # Material 3 theme
```

---

## 🗄️ Database Schema

```
users            → id, name, email, password, role
categories       → id, name
exams            → id, title, category_id, duration, passPercentage, totalScore
questions        → id, exam_id, text, options, correctAnswer, mark
submitted_exams  → id, exam_id, student_id, score, grade, status, date
answer_snapshots → id, submitted_exam_id, questionText, options,
                   correctAnswerIndex, studentAnswerIndex, examMark
```

> `answer_snapshots` stores a frozen copy of each question at submission time — so exam reviews remain accurate even if questions are later edited.

---

## 🛠️ Tech Stack

| Category | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| DI | Hilt |
| Navigation | Navigation Compose |
| Local DB | Room |
| Async | Kotlin Coroutines + Flow |
| State | StateFlow + SharedFlow |

---

## 👥 Roles

| Feature | Student | Admin |
|---|---|---|
| Register / Login | ✅ | ✅ |
| Browse categories | ✅ | ✅ |
| Take exam with timer | ✅ | ❌ |
| Review past answers | ✅ | ❌ |
| Track progress history | ✅ | ❌ |
| Create / delete exams | ❌ | 🚧 |
| Add questions | ❌ | 🚧 |
| View top students | ❌ | 🚧 |

> ⚠️ Admin flow is currently in progress. Student flow is fully functional.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+
- Android device or emulator (API 26+)

### Setup

1. **Clone the repo**
```bash
git clone https://github.com/yasminhosam/Exam-app.git
cd Exam-app
```

2. **Build and run**
   - Open in Android Studio
   - Sync Gradle
   - Run on emulator or device

> No API keys or external services required — fully local with Room database.

---

## 🔮 Future Improvements

- [ ] Complete Admin flow (create exams, manage questions, leaderboard)
- [ ] Hash passwords before storing *(currently plain text — not production ready)*
- [ ] Persist login session across app restarts
- [ ] Export results as PDF
- [ ] Question bank reuse across multiple exams

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
```

---

> Built with ❤️ using Jetpack Compose
