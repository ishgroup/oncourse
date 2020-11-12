;-----------------------------------
;Search the common/lib directory for plugins
!addplugindir ../../../../lib/nsisplugin

;Use the Modern User Interface2
!include MUI2.nsh
!include "FileFunc.nsh"

;-----------------------------------
;General

!define APPNAME "onCourseServer"
!define ARP "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\${APPNAME}"
Name ${APPNAME}
OutFile "${APPNAME}-installer.exe"
InstallDir $PROGRAMFILES\onCourse
BrandingText "ish onCourse"
RequestExecutionLevel admin


;--------------------------------
;Interface settings
;
;!define MUI_ICON icon_file
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP header.bmp
;(recommended size: 150x57 pixels).
!define MUI_WELCOMEFINISHPAGE_BITMAP welcome.bmp
;(recommended size: 164x314 pixels)
;!define MUI_UNWELCOMEFINISHPAGE_BITMAP bmp_file (recommended size: 164x314 pixels).


;--------------------------------

; Pages

;  Examples:

; !insertmacro MUI_PAGE_LICENSE "License.rtf"
; !insertmacro MUI_PAGE_COMPONENTS

; Installer pages
; MUI_PAGE_WELCOME
; MUI_PAGE_LICENSE textfile
; MUI_PAGE_COMPONENTS
; MUI_PAGE_DIRECTORY
; MUI_PAGE_STARTMENU pageid variable
; MUI_PAGE_INSTFILES
; MUI_PAGE_FINISH

; Uninstaller pages
; MUI_UNPAGE_WELCOME
; MUI_UNPAGE_CONFIRM
; MUI_UNPAGE_LICENSE textfile
; MUI_UNPAGE_COMPONENTS
; MUI_UNPAGE_DIRECTORY
; MUI_UNPAGE_INSTFILES
; MUI_UNPAGE_FINISH

!define MUI_WELCOMEPAGE_TITLE "Welcome to the onCourse Server install wizard"
;!define MUI_WELCOMEPAGE_TITLE_3LINES
!define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of onCourse Server.$\n$\n$\nClick Next to continue"


;!define MUI_PAGE_HEADER_TEXT text
!define MUI_PAGE_HEADER_SUBTEXT "Choose the folder in which to install onCourse Server"

!define MUI_DIRECTORYPAGE_TEXT_TOP "Setup will install onCourse Server in the following folder. To install in a different folder, click Browse and select another folder. Click Install to start the Installation."

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

;!define MUI_FINISHPAGE_TITLE ""
!define MUI_FINISHPAGE_TEXT "onCourse Server has been installed. Shortcuts have been added to the Start menu under onCourse."
!define MUI_FINISHPAGE_LINK "http://www.ish.com.au/oncourse/support"
!define MUI_FINISHPAGE_LINK_LOCATION "http://www.ish.com.au/oncourse/support"
!define MUI_FINISHPAGE_SHOWREADME "http://www.ish.com.au/oncourse/support"
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;---------------------------------
;Language settings
!insertmacro MUI_LANGUAGE "English"

;---------------------------------
; Install
Section

  SetShellVarContext all
 
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  ; Put file there
  File ${APPNAME}.exe

  CreateDirectory "$SMPROGRAMS\\onCourse"
  CreateShortCut "$SMPROGRAMS\\onCourse\\${APPNAME}.lnk" "$INSTDIR\\${APPNAME}.exe"
  CreateShortCut "$SMPROGRAMS\\onCourse\\Remove ${APPNAME}.lnk" "$INSTDIR\\uninstallServer.exe"
  CreateShortCut "$DESKTOP\\${APPNAME}.lnk" "$INSTDIR\\${APPNAME}.exe" ""
  # define uninstaller name
  writeUninstaller $INSTDIR\\uninstallServer.exe
 
  ${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
  IntFmt $0 "0x%08X" $0
  WriteRegDWORD HKLM "${ARP}" "EstimatedSize" "$0"

	WriteRegStr HKLM "${ARP}" "DisplayName" "onCourse Server"
	WriteRegStr HKLM "${ARP}" "UninstallString" "$\"$INSTDIR\\uninstallServer.exe$\""
	WriteRegStr HKLM "${ARP}" "QuietUninstallString" "$\"$INSTDIR\\uninstallServer.exe$\" /S"
	
	nsisFirewall::AddAuthorizedApplication "$INSTDIR\\${APPNAME}.exe" "onCourse Server"
SectionEnd
 
;---------------------------------
section "Uninstall"

  SetShellVarContext all
 
  # Always delete uninstaller first
  delete $INSTDIR\\uninstallServer.exe
 
  # now delete installed file
  delete $INSTDIR\\${APPNAME}.exe

  # remove start menu link and directory
  Delete "$SMPROGRAMS\\onCourse\\${APPNAME}.lnk"
  Delete "$SMPROGRAMS\\onCourse\\Remove ${APPNAME}.lnk"
  Delete "$DESKTOP\\${APPNAME}.lnk"
 
  DeleteRegKey HKLM "${ARP}"

	nsisFirewall::RemoveAuthorizedApplication "$INSTDIR\\${APPNAME}.exe"
sectionEnd
