# 🏷️ NicknameMod – Mod Minecraft RP

![Forge 1.16.5](https://img.shields.io/badge/Forge-1.16.5-blue)
![Status: WIP](https://img.shields.io/badge/Status-Stable-lightgreen)
![License: All rights reserved](https://img.shields.io/badge/License-All_rights_reserved-red)

> Un mod Forge 1.16.5 permettant d'attribuer des **noms et prénoms RP** aux joueurs, avec affichage dans le chat et au-dessus de la tête.

---

## ✨ Fonctionnalités

- 👤 Chaque joueur peut avoir un **pseudo RP** (prénom + nom)
- 💬 Les messages envoyés utilisent le **nom RP** au lieu du pseudo Minecraft
- 🧠 Le nom RP s’affiche aussi **au-dessus de la tête** du joueur
- 💾 Données synchronisées client ↔ serveur, persistées en JSON

---

## ⚠️ Limitations et compatibilité

> ❗ Ce mod **n'est pas plugin-friendly** :  
> Il peut **entrer en conflit** avec d'autres **mods ou plugins** qui :
> - gèrent les pseudos (ex: Essentials, TabList, NameTagEdit)
> - modifient l’affichage au-dessus des joueurs
> - interceptent ou modifient le chat

**Utilisation à vos risques et périls si vous l’intégrez à un serveur hybride Forge + Plugins.**

---

## 📁 Structure du mod

- `commands/` → Commandes RP pour définir ou réinitialiser son nom
- `events/` → Hook du chat et des messages pour afficher les bons noms
- `storage/` → Map UUID ↔ Nom RP (persisté en JSON)
- `render/` → Custom rendering du nom au-dessus des joueurs
- `network/` → Packets client/serveur pour synchro des noms
- `util/` → Méthodes utilitaires

---

## 📄 Licence

> Ce projet est protégé par droits d’auteur.  
> **Toute utilisation, modification ou distribution sans mon accord explicite est interdite.**  
> Voir le fichier [LICENSE](LICENSE).
