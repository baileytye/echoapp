# Echo Application
![1](https://github.com/ispam/echoapp/blob/master/gif/echoapp.gif)


Simple Text Echo App built as an Android take home project using Kotlin, Jetpack Compose, Jetpack Navigation, Hilt, Retrofit, Coroutines, and unit tests.

The app lets the user enter text, submit it for simulated server validation, show a loading state, and navigate to a result screen that displays either a successful response or a server validation error.

---

## Project objective

Create a simple Text Echo app with the following behavior:

- Provide a way for the user to input text.
- Provide a way to submit the text.
- Pretend to validate the input with an external server.
- If validation succeeds, show the submitted text.
- If validation fails, show an error.
- Add unit tests to validate the behavior.
---

## Libraries

- Kotlin
- Jetpack Compose
- Jetpack Navigation
- Hilt
- Retrofit
- OkHttp
- Kotlin Coroutines
- JUnit
- kotlinx-coroutines-test

---

## Project structure

```text
app/
└── src/main/java/com/example/echoaaplication/
    ├── EchoApplication.kt
    ├── MainActivity.kt
    │
    ├── presentation/
    │   ├── EchoDestination.kt
    │   ├── EchoNavHost.kt
    │   ├── NavigationEvent.kt
    │   ├── EchoViewModel.kt
    │
    ├── domain/
    │   ├── EchoRepository.kt
    │   ├── EchoResult.kt
    │   └── SubmitUseCase.kt
    │
    ├── data/
    │   ├── remote/
    │   │   ├── EchoApi.kt
    │   │   ├── EchoRemoteDataSource.kt
    │   │   └── FakeServerInterceptor.kt
    │   └── repository/
    │       └── EchoRepositoryImpl.kt
    │
    └── di/
        ├── NetworkModule.kt
        └── RepositoryModule.kt
```

## License

Copyright [2026] [Diego Fernando Urrea Gutiérrez]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.