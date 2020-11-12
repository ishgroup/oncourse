;-----------------------------------
;Search the common/lib directory for plugins
!addplugindir ../../../../lib/nsisplugin

;Use the Modern User Interface2
!include MUI2.nsh
!include "FileFunc.nsh"

;-----------------------------------
;General

!define APPNAME "onCourseService64"
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

!define MUI_WELCOMEPAGE_TITLE "Welcome to the onCourse Server as a Service install wizard"
;!define MUI_WELCOMEPAGE_TITLE_3LINES
!define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of onCourse Server as a Windows Service.$\n$\n$\nClick Next to continue"

;!define MUI_PAGE_HEADER_TEXT text
!define MUI_PAGE_HEADER_SUBTEXT "Choose the folder in which to install onCourse Service"

!insertmacro MUI_PAGE_WELCOME
;--------------------------------
!define MUI_DIRECTORYPAGE_TEXT_TOP "Setup will install onCourse Service in the following folder. To install in a different folder, click Browse and select another folder."
!insertmacro MUI_PAGE_DIRECTORY
;--------------------------------
!define MUI_PAGE_HEADER_SUBTEXT "Choose the folder in which your data file will be stored"
!define MUI_DIRECTORYPAGE_TEXT_TOP "Please choose a location for your data file. This directory and it's contents will never be removed by uninstall process to preserve your data. If you wish to use an external database, you can put a URI here."
!define MUI_DIRECTORYPAGE_TEXT_DESTINATION "Data File Directory or External Database URI"
;A string to hold either our data dir or dburi
Var DataDir
Var DBURI
!define MUI_DIRECTORYPAGE_VARIABLE $DataDir
!define MUI_PAGE_CUSTOMFUNCTION_PRE setDefaultData
;Does not disable the Next/Install button when a folder is invalid
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE "DirectoryLeave"
!define MUI_DIRECTORYPAGE_VERIFYONLEAVE
!insertmacro MUI_PAGE_DIRECTORY
;--------------------------------
!insertmacro MUI_PAGE_INSTFILES
;--------------------------------
;!define MUI_FINISHPAGE_TITLE ""
!define MUI_FINISHPAGE_TEXT "onCourse Server has been installed as a service and has been started. There is no need to restart your machine."
!define MUI_FINISHPAGE_LINK "http://www.ish.com.au/oncourse/support"
!define MUI_FINISHPAGE_LINK_LOCATION "http://www.ish.com.au/oncourse/support"
!define MUI_FINISHPAGE_SHOWREADME "http://www.ish.com.au/oncourse/support"
;--------------------------------
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;---------------------------------
;Language settings
!insertmacro MUI_LANGUAGE "English"

;Set the default location for data file before the user overrides it
;Detect if the user installing already has an existing server config file
;and skip the data directory page if it exists

Function setDefaultData
 StrCpy $DataDir "$PROGRAMFILES\\onCourse"
 IfFileExists $INSTDIR\\onCourse.cfg ConfigExists NoConfig
 ConfigExists:
	Abort
 NoConfig:
 
FunctionEnd

;Directory pages by default do some manipulation to strip out ':' which are needed for
;our dburi
Function DirectoryLeave
  StrCpy $DBURI $DataDir
FunctionEnd

;---------------------------------
; Install
Section

  SetShellVarContext all
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

   ; Put file there
  File ${APPNAME}.exe

  CreateDirectory "$SMPROGRAMS\\onCourse"
  CreateShortCut "$SMPROGRAMS\\onCourse\\Remove ${APPNAME}.lnk" "$INSTDIR\\uninstallService64.exe"

  #Call commandline of our exe4j to install
  ExecWait '"$INSTDIR/${APPNAME}.exe" /install'
  StrCpy $DataDir $DBURI
  
  ;If the user has does not have an existing config file, we create one with 
  ;the data dir they have chosen.
  IfFileExists $INSTDIR\\onCourse.cfg ConfigExists NoConfig
  NoConfig:
	FileOpen $4 "$INSTDIR\\onCourse.cfg" w
  	FileWrite $4 "db=$DataDir"
  	FileWrite $4 "$\r$\n" ; we write a new line
  	FileClose $4
  ConfigExists:
  
  SimpleSC::StartService "onCourseServer" "" 30
  writeUninstaller $INSTDIR\\uninstallService64.exe

  ${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
  IntFmt $0 "0x%08X" $0
  WriteRegDWORD HKLM "${ARP}" "EstimatedSize" "$0"

	WriteRegStr HKLM "${ARP}" "DisplayName" "onCourse Service"
	WriteRegStr HKLM "${ARP}" "UninstallString" "$\"$INSTDIR\\uninstallService64.exe$\""
	WriteRegStr HKLM "${ARP}" "QuietUninstallString" "$\"$INSTDIR\\uninstallService64.exe$\" /S"
	
	nsisFirewall::AddAuthorizedApplication "$INSTDIR\\${APPNAME}.exe" "onCourse Server"
SectionEnd
 
;---------------------------------
section Uninstall

  SetShellVarContext all

  SimpleSC::StopService "onCourseServer" 1 30

  #call commandline of exe4j to uninstall
  ExecWait '"$INSTDIR/${APPNAME}.exe" /uninstall'
 
  # Always delete uninstaller first
  Delete $INSTDIR\\uninstallService64.exe
 
  # now delete installed file
  Delete $INSTDIR\\${APPNAME}.exe

  # remove start menu link and directory
  Delete "$SMPROGRAMS\\onCourse\\Remove ${APPNAME}.lnk"

  DeleteRegKey HKLM "${ARP}"

	nsisFirewall::RemoveAuthorizedApplication "$INSTDIR\\${APPNAME}.exe"
sectionEnd