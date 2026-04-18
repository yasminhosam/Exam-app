# 📝 Online Examination System

A full-featured Android exam management app built with **Jetpack Compose** and **MVVM architecture**. Supports two roles — **Teacher** to create and manage exams, and **Student** to take exams, track progress, and review answers. Offline-first with automatic background sync to Firebase.

---

## 📱 Screenshots

<table>
  <tr>
    <td align="center"><b>Login</b></td>
    <td align="center"><b>Student Home</b></td>
    <td align="center"><b>Exam</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/8f604518-0602-4c30-abcc-0a66aaf5947d" width="220"/></td>
     <td><img src="https://github.com/user-attachments/assets/75e1ff32-8ffb-48c7-ad89-d2688d48aed2" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/72a72e77-ce7a-47e6-99a0-a9d492d643f8" width="220"/></td>
  </tr>
  <tr>
    <td align="center"><b>Progress</b></td>
    <td align="center"><b>Review Result</b></td>
    <td align="center">Teacher Home</td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/8042aac6-c660-4cb1-b5fb-2866a9ea2758" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/15806e76-ce6e-4856-80d7-6c660e12afc4" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/010672cc-8ce7-48e8-a455-95a5f307ffba" width="220"/>
</td>
  </tr>
  <tr>
    <td align="center"><b>Create Exam</b></td>
    <td align="center"><b>Top Students</b></td>
    <td align="center">Profile</td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/ded70d0b-3c99-467b-b288-03ff61ade6f9" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/87e6686f-5358-4641-aed8-9e2752868022" width="220"/></td>
    <td><img src="https://github.com/user-attachments/assets/b6d9b799-e4eb-4f79-a66a-741f6e098301" width="220"/>
</table>

---

## ✨ Features

### 🎓 Student
- **Register & Login** — Role-based authentication via Firebase Auth with form validation (email format, password strength)
- **Browse Exams by Category** — Exams organized into categories: Science, Math, Programming, Geography, Physics, Biology, History, Art, and more
- **Take Exam** — Answer multiple-choice questions with a live circular countdown timer
- **Auto Submit** — Exam auto-submits when the timer runs out
- **Results** — Instant score, grade (A/B/C/D/F), and pass/fail status after submission
- **Review Answers** — Step through each question post-exam with correct vs. selected answer highlighted
- **Progress Tracking** — Full history of all submitted exams with score, grade, date, and pass/fail badge

### 🛠️ Teacher
- **Dashboard** — Overview of all created exams with question count, duration, and pass percentage
- **Create Exams** — Add exams with title, duration, pass percentage, and category
- **Add Questions** — Multiple-choice questions with configurable marks per question
- **Delete Exams** — Remove exams with confirmation dialog (syncs deletion to Firestore)
- **Top Students Leaderboard** — View highest-scoring students across all exams

---

## 🏗️ Architecture

```
app/
├── data/
│   ├── local/
│   │   ├── dao/          # ExamDao, StudentDao
│   │   ├── AppDatabase   # Room database
│   │   └── Converters    # Type converters (Duration, List<String>)
│   ├── remote/           # Firestore DTOs (ExamDto, QuestionDto, UserDto, SubmittedExamDto...)
│   ├── mapper/           # Domain model ↔ DTO mappers
│   └── repository/       # AuthRepositoryImpl, ExamRepositoryImpl, StudentRepositoryImpl
├── domain/
│   ├── model/            # Exam, Question, Category, User, AnswerSnapshot, SubmittedExam...
│   ├── repository/       # Repository interfaces
│   ├── GradeCalculator   # Grade letter logic (A/B/C/D/F)
│   └── SyncWorker        # WorkManager worker for offline → Firestore sync
├── di/                   # Hilt modules (AppModule, RepositoryModule)
└── ui/
    ├── auth/             # Login & Register screens + AuthViewModel
    ├── student/          # StudentHomeScreen + MainViewModel
    ├── teacher/          # TeacherDashboardScreen + dashboardViewModel
    ├── exam/             # ExamListScreen, ExamScreen, CreateExamScreen, ReviewExamScreen
    ├── progress/         # ProgressScreen + ProgressViewModel
    ├── profile/          # ProfileScreen + ProfileViewModel
    ├── topstudent/       # TopStudentsScreen + TopStudentsViewModel
    ├── navigation/       # NavGraph, Screen routes
    └── theme/            # Material 3 theme (Color, Type, Theme)
```

---

## 🗄️ Database Schema

```
categories       → id, name, imageRes
users            → id, name, email, password, role (STUDENT | TEACHER)
exams            → id, title, category_id, teacher_id, duration, passPercentage, totalScore, isSynced, isDeleted
questions        → id, exam_id, text, options (List<String>), correctAnswer, mark, isSynced
submitted_exams  → id, exam_id, student_id, score, grade, status, date, isSynced
answer_snapshots → id, submitted_exam_id, questionText, options,
                   correctAnswerIndex, studentAnswerIndex, examMark, isSynced
```

> `answer_snapshots` stores a frozen copy of each question at submission time — so exam reviews remain accurate even if questions are later edited.

---

## ☁️ Offline-First Sync

The app uses an **offline-first** approach:
- All data is written to **Room** (local DB) first
- A **WorkManager** background job (`SyncWorker`) periodically syncs pending records to **Firebase Firestore**
- Exams, questions, submissions, and answer snapshots each track an `isSynced` flag
- Deleted exams are soft-deleted locally and removed from Firestore by the sync worker

---

## 🛠️ Tech Stack

| Category | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| DI | Hilt (Dagger) |
| Navigation | Navigation Compose |
| Local DB | Room |
| Remote DB | Firebase Firestore |
| Auth | Firebase Authentication |
| Background Sync | WorkManager (HiltWorker) |
| Async | Kotlin Coroutines + Flow |
| State | StateFlow + SharedFlow |
| Serialization | Gson |

---

## 👥 Roles

| Feature | Student | Teacher |
|---|---|---|
| Register / Login | ✅ | ✅ |
| Browse categories | ✅ | — |
| Take exam with timer | ✅ | — |
| Auto-submit on timeout | ✅ | — |
| Review past answers | ✅ | — |
| Track progress history | ✅ | — |
| Create / delete exams | — | ✅ |
| Add multiple-choice questions | — | ✅ |
| View top students leaderboard | — | ✅ |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11+
- Android device or emulator (API 26+)
- A Firebase project with **Firestore** and **Authentication** enabled

### Setup

1. **Clone the repo**
```bash
git clone https://github.com/yasminhosam/Exam-app.git
cd Exam-app
```

2. **Connect Firebase**
   - Create a project at [Firebase Console](https://console.firebase.google.com/)
   - Enable **Email/Password** authentication
   - Enable **Cloud Firestore**
   - Download `google-services.json` and place it in `app/src/`

3. **Build and run**
   - Open in Android Studio
   - Sync Gradle
   - Run on emulator or device

---

## 🔮 Future Improvements

- [ ] Hash passwords before storing *(currently plain text — not production ready)*
- [ ] Persist login session across app restarts
- [ ] Push notifications for new exams
- [ ] Export results as PDF
- [ ] Question bank reuse across multiple exams
- [ ] Real-time exam updates via Firestore listeners

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
```

---

> Built with ❤️ using Jetpack Compose
