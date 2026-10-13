# 💸 Splitin

**Splitin** is an Android app that splits group bills fairly.

Ever had a group meal where one person paid for everything, and then nobody knew how to divide it? Everyone orders different things, and then there's tax on top. Splitin calculates each person's share based on what they actually ordered, tax included, so nobody overpays and nobody has to do the math by hand.

## The Problem

- Splitting evenly is often unfair because everyone orders differently
- Manual calculation is tedious and error-prone
- Tax is often forgotten or divided carelessly
- The person who covered the bill has no clear record of who owes what

## Key Features

- Add everyone who joins the bill
- Enter items with prices and assign who ordered what
- Choose who paid at the register
- Tax is shared proportionally based on each person's spending
- Clear final result: who owes how much to whom

## App Flow (6 Activities)

| No | Activity | Purpose |
|----|----------|---------|
| 1 | **Home** | Landing screen, start a new split |
| 2 | **Add Friends** | Add friends joining the bill |
| 3 | **Add Items** | Enter menu items, prices, and who ordered them |
| 4 | **Who Paid** | Select the person who covered the payment |
| 5 | **Total** | Summary of the total bill including tax |
| 6 | **Split Result** | Final breakdown of what each person owes |

## Calculation Example

Andi, Budi, and Citra eat together. Tax is 10%.

| Name | Order | Price | Tax 10% | Total to Pay |
|------|-------|-------|---------|--------------|
| Andi | Fried rice | Rp30,000 | Rp3,000 | Rp33,000 |
| Budi | Chicken noodles + iced tea | Rp25,000 | Rp2,500 | Rp27,500 |
| Citra | Iced tea | Rp5,000 | Rp500 | Rp5,500 |

If Andi paid for everything, Budi owes Andi Rp27,500 and Citra owes Andi Rp5,500.

## Screenshots

| Home | Add Friends | Add Items |
|:----:|:-----------:|:---------:|
| <img src="https://github.com/user-attachments/assets/d9156f20-77a1-4e1a-b6a9-6d8c16584f86" width="220" alt="Home" /> | <img src="https://github.com/user-attachments/assets/01091c6b-d03b-453a-a2ff-02cbfd6a40b0" width="220" alt="Add Friends" /> | <img src="https://github.com/user-attachments/assets/b0474433-e609-4708-a9de-efbbb7fb37b7" width="220" alt="Add Items" /> |

| Who Paid | Total | Split Result |
|:--------:|:-----:|:------------:|
| <img src="https://github.com/user-attachments/assets/66175daa-b576-4572-9584-eed77bdda520" width="220" alt="Who Paid" /> | <img src="https://github.com/user-attachments/assets/eab4295e-bff1-4a89-bee2-ec02e149f328" width="220" alt="Total" /> | <img src="https://github.com/user-attachments/assets/f8d7b005-2881-4ede-b852-a7a88e7d659c" width="220" alt="Split Result" /> |

## Tech Stack

- Platform: Android
- Language: Kotlin
- UI: Jetpack Compose
- IDE: Android Studio

## Getting Started

1. Clone the repository
```bash
   git clone https://github.com/Aderocky/Splitin.git
```
2. Open the project in Android Studio
3. Wait for Gradle sync to finish
4. Run on an emulator or a physical device (Run ▶️)

## Roadmap

- [ ] Save split history
- [ ] Share results via WhatsApp
- [ ] Support discounts and service charges
- [ ] Automatic receipt scanning

## Author

**Ade Rocky Saputra**
GitHub: [@Aderocky](https://github.com/Aderocky)
