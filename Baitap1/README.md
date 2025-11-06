# 📱 Portfolio Android – Vũ Văn Thông

> Ứng dụng Android (Jetpack Compose) giới thiệu **Vũ Văn Thông** – SV năm 3 **Ngành An toàn thông tin, HCMUTE**.  
> Giao diện hiện đại: splash mượt, nền gradient + hiệu ứng “bubbles”, thẻ glassmorphism, chips kỹ năng và các nút hành động liên hệ/portfolio.

---

## ✨ Tính năng nổi bật

- 🌊 **Splash** chuyển cảnh mượt (scale + fade).
- 🎨 **Nền animated** dạng bubbles tạo chiều sâu trên gradient tối.
- 🧊 **Glass card** hiển thị thông tin:
  - **Vũ Văn Thông** – SV năm 3 ATTT – **HCMUTE**.
- 🏷️ **Chips kỹ năng**: An toàn thông tin, CTF, Pentest, Network, Android/Kotlin.
- 🔗 **CTA**: Portfolio / Gọi / Email / Copy email.

---

## 🎬 Ảnh chụp màn hình

| Splash | Intro | |gg|
|---|---|
| ![Splash](art/1.png) | ![Intro](art/2.png) |   ![gg](art/3.png) |

---

## 🚀 Cài đặt & Chạy

### Android Studio
1. Clone repo và mở thư mục gốc bằng Android Studio.
2. Chọn **AVD** (Device Manager → Create device…) hoặc **điện thoại thật** rồi **Run ▶**.

### Dòng lệnh
```bash
./gradlew assembleDebug
./gradlew installDebug
```

---

## 🧩 Công nghệ
Kotlin · Jetpack Compose · Material 3 · Compose Animation (animateFloatAsState/rememberInfiniteTransition) · Intent (ACTION_VIEW, ACTION_DIAL, ACTION_SENDTO)

---

## 🛠️ Tuỳ chỉnh nhanh
Trong `MainActivity.kt`:
```kotlin
private const val EMAIL = "vuvanthong1708@gmail.com"
private const val PHONE = "0968046024"
private const val PORTFOLIO = "https://vanity1412.github.io/portfolio_VVT/"
```

Tên app: `res/values/strings.xml`
```xml
<string name="app_name">Vũ Văn Thông - Portfolio</string>
```

---

## 🙋‍♂️ Liên hệ
- **Portfolio**: https://vanity1412.github.io/portfolio_VVT/
- **Email**: vuvanthong1708@gmail.com
- **SĐT**: 0968046024

---

## 📄 License
MIT
