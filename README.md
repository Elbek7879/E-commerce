# E-commerce Spring Boot Project


Bu loyiha **E-commerce** tizimi uchun backendni tashkil qiladi.  
U mahsulotlar va buyurtmalarni boshqarish uchun REST API taqdim etadi.  

**Asosiy funksiyalar:**
- Mahsulotlar: qo‘shish, o‘chirish, yangilash, qidirish
- Buyurtmalar: yaratish, o‘chirish, statusni yangilash, mijoz bo‘yicha buyurtmalarni ko‘rish
- Mahsulot stokini avtomatik boshqarish
- Buyurtma email validatsiyasi va duplicate itemlarni tekshirish

Texnologiyalar

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Lombok
- H2 (test uchun) / MySQL (ishlab chiqarish uchun)
- JUnit 5 & Mockito
- Swagger (API dokumentatsiyasi)
- Maven
  

Ishga tushirish

**Maven yordamida build qilish:**
```bash
mvn clean install

Applicationni ishga tushirish:
mvn spring-boot:run

Application porti:
http://localhost:8080

API dokumentatsiyasi (Swagger):
http://localhost:8080/swagger-ui.html

postman orqali ham tekshirish mumkin

Unit test qilib ham tekshirish mumkin
  


