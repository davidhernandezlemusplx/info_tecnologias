# Guía rápida de WSL (Windows Subsystem for Linux)

![raw](../../images/interes/wsl.png)

## Tabla de Contenidos

- [Guía rápida de WSL (Windows Subsystem for Linux)](#guía-rápida-de-wsl-windows-subsystem-for-linux)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [1. Ver distribuciones disponibles para instalar](#1-ver-distribuciones-disponibles-para-instalar)
  - [2. Instalar una distribución](#2-instalar-una-distribución)
  - [3. Iniciar la distribución recién instalada](#3-iniciar-la-distribución-recién-instalada)
  - [4. Ver distribuciones instaladas localmente](#4-ver-distribuciones-instaladas-localmente)
  - [5. Eliminar una distribución instalada](#5-eliminar-una-distribución-instalada)
  - [6. Primer inicio tras instalación](#6-primer-inicio-tras-instalación)
  - [7. Introducir al usuario al grupo root (opcional)](#7-introducir-al-usuario-al-grupo-root-opcional)
  - [8. Actualizar y preparar entorno base](#8-actualizar-y-preparar-entorno-base)
  - [9. (Opcional) Definir carpeta de inicio por defecto al lanzar la distro](#9-opcional-definir-carpeta-de-inicio-por-defecto-al-lanzar-la-distro)
  - [10. Solución de errores comunes al iniciar WSL](#10-solución-de-errores-comunes-al-iniciar-wsl)
  - [Volver al README](#volver-al-readme)

## 1. Ver distribuciones disponibles para instalar

```powershell
wsl --list --online
```

---

## 2. Instalar una distribución

Instalación:

```powershell
wsl --install <NombreDistribucion> [--name <NombrePersonalizado>]
```

Ejemplo:

```powershell
wsl --install Ubuntu-24.04 --name MyUbuntu
```

> Esto te permite tener varias versiones o entornos con distintos nombres.

---

## 3. Iniciar la distribución recién instalada

```powershell
wsl -d <NombreDistribucion>
```

Ejemplo:

```powershell
wsl -d MyUbuntu
```

---

## 4. Ver distribuciones instaladas localmente

```powershell
wsl --list --verbose
```

---

## 5. Eliminar una distribución instalada

```powershell
wsl --unregister <NombreDistribucion>
```

Ejemplo:

```powershell
wsl --unregister MyUbuntu
```

---

## 6. Primer inicio tras instalación

Al iniciar WSL por primera vez con una distribución instalada, el sistema te pedirá:

1. Escribir un **nombre de usuario** (se creará una cuenta nueva).  
2. Introducir una **contraseña** para ese usuario.

---

## 7. Introducir al usuario al grupo root (opcional)

Cámbiate a root primero:

```bash
su
```

Luego crea el usuario y añade a sudo:

```bash
usermod -aG sudo nombre_usuario
```

Opcional:

```bash
echo '%sudo ALL=(ALL:ALL) ALL' >> /etc/sudoers
```

---

## 8. Actualizar y preparar entorno base

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git sudo
```

---

## 9. (Opcional) Definir carpeta de inicio por defecto al lanzar la distro

Desde PowerShell:

```powershell
wsl -d MyUbuntu --cd "D:\Proyectos"
```

## 10. Solución de errores comunes al iniciar WSL

Error:

```powershell
Error de inicio de sesión: el usuario no tiene permisos para el tipo de inicio de sesión solicitado en el equipo.
Código de error: Wsl/Service/CreateInstance/CreateVm/HCS/0x80070569
```

Causa:
Este error suele aparecer cuando el servicio de virtualización no está habilitado correctamente o no se poseen los permisos necesarios.

Solución:
Ejecuta PowerShell como administrador y lanza estos comandos uno a uno:

```powershell
Get-Service vmcompute | Restart-Service
```

> Get-Service vmcompute | Restart-Service reinicia el servicio de máquinas virtuales

```powershell
gpupdate /force
```

Salida:

```powershell
Actualizando directiva...

La actualización de la directiva de equipo se completó correctamente.
Se completó correctamente la Actualización de directiva de usuario.
```

> gpupdate /force actualiza las políticas de grupo, aplicando cambios de permisos

Después de esto, reinicia tu equipo si te lo pide, y vuelve a intentar iniciar la distribución WSL.

## [Volver al README](../README.md)
