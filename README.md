# Netparkoló – Backend

Ez a könyvtár tartalmazza a Netparkoló alkalmazás szerveroldali logikáját. A rendszer egy Spring Boot alapú, modern RESTful API, amely felelős az üzleti logikáért, a biztonságos hitelesítésért, az adatbázis-kezelésért és a fizetési tranzakciók lebonyolításáért.

## Használt Technológiák

- **Java 17**
- **Spring Boot 3.x**
  - Spring Web
  - Spring Data JPA
  - Spring Security
- **MySQL**
- **JSON Web Token**
- **Stripe API**
- **Lombok**
- **JUnit 5 & Mockito**

## Főbb Funkciók

1. **Felhasználókezelés és Hitelesítés:**
   - JWT alapú bejelentkezés és regisztráció.
   - Jelszó-visszaállítás.
   - OAuth2 bejelentkezés támogatása.
2. **Parkolásmenedzsment:**
   - Fix férőhelyes és zónaalapú parkolók kezelése.
   - Valós idejű kapacitás-ellenőrzés.
   - Parkolási díjak dinamikus, percalapú kalkulációja.
3. **Fizetési Rendszer:**
   - Stripe Checkout Session generálása biztonságos online fizetéshez.
4. **Adminisztráció:**
   - Parkolók, zónák és felhasználók teljes körű adatkezelése dedikált admin végpontokon keresztül.

## Telepítés és Futtatás Helyi Környezetben

### Előfeltételek
- **Java 17 JDK**
- **Maven**
- **MySQL Server**

### 1. Adatbázis beállítása
Hozd létre az üres adatbázist a MySQL szervereden:
```sql
CREATE DATABASE parkolo_projekt;
