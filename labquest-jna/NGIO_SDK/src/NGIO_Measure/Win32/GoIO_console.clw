; CLW file contains information for the MFC ClassWizard

[General Info]
Version=1
LastClass=CAboutDlg
LastTemplate=CDialog
NewFileInclude1=#include "stdafx.h"
NewFileInclude2=#include "goio_console.h"
LastPage=0

ClassCount=6
Class1=CGoIO_consoleApp
Class2=CAboutDlg
Class3=CGoIO_consoleDoc
Class4=CGoIO_consoleView
Class5=CMainFrame
Class6=CSetMeasPeriodDlg

ResourceCount=8
Resource1=IDD_SET_MEASUREMENT_PERIOD
Resource2=IDD_DIALOG2
Resource3=IDD_ABOUTBOX
Resource4=IDR_MAINFRAME
Resource5=IDD_DIALOG3
Resource6=IDD_DIALOG1
Resource7=IDD_DIALOG4
Resource8=IDD_PACKET_BAR

[CLS:CGoIO_consoleApp]
Type=0
BaseClass=CWinApp
HeaderFile=GoIO_console.h
ImplementationFile=GoIO_console.cpp
LastObject=CGoIO_consoleApp

[CLS:CAboutDlg]
Type=0
BaseClass=CDialog
HeaderFile=GoIO_console.cpp
ImplementationFile=GoIO_console.cpp
Filter=D
VirtualFilter=dWC
LastObject=CAboutDlg

[CLS:CGoIO_consoleDoc]
Type=0
BaseClass=CDocument
HeaderFile=GoIO_consoleDoc.h
ImplementationFile=GoIO_consoleDoc.cpp

[CLS:CGoIO_consoleView]
Type=0
BaseClass=CView
HeaderFile=GoIO_consoleView.h
ImplementationFile=GoIO_consoleView.cpp

[CLS:CMainFrame]
Type=0
BaseClass=CFrameWnd
HeaderFile=MainFrm.h
ImplementationFile=MainFrm.cpp
Filter=T
VirtualFilter=fWC
LastObject=ID_APP_ABOUT

[CLS:CSetMeasPeriodDlg]
Type=0
BaseClass=CDialog
HeaderFile=SetMeasPeriodDlg.h
ImplementationFile=SetMeasPeriodDlg.cpp

[DLG:IDD_ABOUTBOX]
Type=1
Class=CAboutDlg
ControlCount=4
Control1=IDC_STATIC,static,1342177283
Control2=IDC_STATIC,static,1342308480
Control3=IDOK,button,1342373889
Control4=IDC_LIB_VERSION,static,1342308352

[DLG:IDD_SET_MEASUREMENT_PERIOD]
Type=1
Class=CSetMeasPeriodDlg
ControlCount=4
Control1=IDOK,button,1342242817
Control2=IDCANCEL,button,1342242816
Control3=IDC_PERIOD_LABEL,static,1342308352
Control4=IDC_PERIOD,edit,1350639744

[MNU:IDR_MAINFRAME]
Type=1
Class=?
Command1=ID_FILE_NEW
Command2=ID_APP_EXIT
Command3=ID_EDIT_UNDO
Command4=ID_EDIT_CUT
Command5=ID_EDIT_COPY
Command6=ID_EDIT_PASTE
Command7=IDM_DEVICE0
Command8=IDM_DEVICE1
Command9=IDM_DEVICE2
Command10=IDM_DEVICE3
Command11=IDM_DEVICE4
Command12=IDM_DEVICE5
Command13=IDM_DEVICE6
Command14=IDM_DEVICE7
Command15=IDC_ANALOG1
Command16=IDC_ANALOG1_10V
Command17=IDC_ANALOG2
Command18=IDC_ANALOG3
Command19=IDC_ANALOG4
Command20=ID_CHANNELS_DIGITAL1
Command21=ID_CHANNELS_DIGITAL1_MOTION
Command22=ID_CHANNELS_DIGITAL1_ROTARY
Command23=ID_CHANNELS_DIGITAL1_ROTARY_X4
Command24=ID_CHANNELS_DIGITAL1_RADIATION
Command25=ID_CHANNELS_DIGITAL2
Command26=ID_CHANNELS_DIGITAL2_MOTION
Command27=ID_CHANNELS_DIGITAL2_ROTARY
Command28=ID_CHANNELS_DIGITAL2_ROTARY_X4
Command29=ID_CHANNELS_DIGITAL2_RADIATION
Command30=ID_CHANNELS_AUDIO_INTERNAL
Command31=IDM_CALIB0
Command32=IDM_CALIB1
Command33=IDM_CALIB2
Command34=IDM_GET_STATUS
Command35=IDM_SET_MEAS_PERIOD
Command36=IDM_START_MEAS
Command37=IDM_STOP_MEAS
Command38=IDM_NGIO_LIB_VERBOSE
Command39=ID_APP_ABOUT
CommandCount=39

[TB:IDR_MAINFRAME]
Type=1
Class=?
Command1=ID_FILE_NEW
Command2=ID_FILE_OPEN
Command3=ID_FILE_SAVE
Command4=ID_EDIT_CUT
Command5=ID_EDIT_COPY
Command6=ID_EDIT_PASTE
Command7=ID_FILE_PRINT
Command8=ID_APP_ABOUT
CommandCount=8

[ACL:IDR_MAINFRAME]
Type=1
Class=?
Command1=ID_FILE_NEW
Command2=ID_FILE_OPEN
Command3=ID_FILE_SAVE
Command4=ID_FILE_PRINT
Command5=ID_EDIT_UNDO
Command6=ID_EDIT_CUT
Command7=ID_EDIT_COPY
Command8=ID_EDIT_PASTE
Command9=ID_EDIT_UNDO
Command10=ID_EDIT_CUT
Command11=ID_EDIT_COPY
Command12=ID_EDIT_PASTE
Command13=ID_NEXT_PANE
Command14=ID_PREV_PANE
CommandCount=14

[DLG:IDD_PACKET_BAR]
Type=1
Class=?
ControlCount=5
Control1=IDC_STATIC,button,1342177287
Control2=IDM_START_MEAS,button,1342242816
Control3=IDM_STOP_MEAS,button,1342242816
Control4=IDC_MEAS,edit,1350633602
Control5=IDC_UNITS,static,1342308352

[DLG:IDD_DIALOG1]
Type=1
Class=?
ControlCount=2
Control1=IDOK,button,1342242817
Control2=IDCANCEL,button,1342242816

[DLG:IDD_DIALOG2]
Type=1
Class=?
ControlCount=2
Control1=IDOK,button,1342242817
Control2=IDCANCEL,button,1342242816

[DLG:IDD_DIALOG3]
Type=1
Class=?
ControlCount=2
Control1=IDOK,button,1342242817
Control2=IDCANCEL,button,1342242816

[DLG:IDD_DIALOG4]
Type=1
Class=?
ControlCount=2
Control1=IDOK,button,1342242817
Control2=IDCANCEL,button,1342242816

