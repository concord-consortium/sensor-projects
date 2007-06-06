// MainFrm.cpp : implementation of the CMainFrame class
//

#include "stdafx.h"
#include "LabPro_console.h"
#include "..\API\LabProUSB_interface.h"
#include "LabPro_consoleDoc.h"
#include "LabPro_consoleView.h"
#include "SetNumChansAndModesDlg.h"

#include "MainFrm.h"

#define TIMER_PERIOD_MS 250

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/*
const int LabPro::kNumTotalChannels = LabPro::kNumAnalogChannels + LabPro::kNumDigSonicChannels;

const int LabPro::kSerialBaud = 38400;
const int LabPro::kInputBufferSize = 4096; // make it larger for live data?

const float LabPro::kStatus_badValue = -1.0f;
const float LabPro::kStatus_verifyValue = 8888.0f;
const float LabPro::kStatus_manualTriggerValue = 1.0f; // "manual" trigger
const float LabPro::kStatus_eventsTriggerValue = 6.0f; // events trigger
const float LabPro::kStatus_idleValue = 1.0f;
const float LabPro::kStatus_busyValue = 3.0f;
const float LabPro::kStatus_doneValue = 4.0f;
const float LabPro::kStatus_qsetupAddValue = 16.0f;
const float LabPro::kStatus_remoteAddValue = 32.0f;

const int LabPro::kNumLPStatusTerms = 17;

const int LabPro::kStatus_versionIndex = 0;
const int LabPro::kStatus_batteryIndex = 2;
const int LabPro::kStatus_verifyIndex = 3;
const int LabPro::kStatus_deltaTIndex = 4;
const int LabPro::kStatus_triggerIndex = 5;
const int LabPro::kStatus_numSamplesIndex = 9;
const int LabPro::kStatus_stateIndex = 13;
const int LabPro::kStatus_firstPtIndex = 14;
const int LabPro::kStatus_lastPtIndex = 15;

const int LabPro::k_nTimeChannel = 0;

const char * LabPro::k_sStatusCmd = "s{7}\r";
const char * LabPro::k_sGetTextCmd = "g{x}\r";
const char * LabPro::k_sResetCmd = "s{0}\r";
const char * LabPro::k_sWakeupCmd = "s\r";
const char * LabPro::k_sStopCmd = "s{6,0}\r";
const char * LabPro::k_sPowerAlwaysOn = "s{102,-1}\r";
const char * LabPro::k_sPowerSaverModeOn = "s{102,0}\r";
const char * LabPro::k_sRestartWithLastSetup = "s{3,-1}\r";
const char * LabPro::k_sTurnOnLED = "s{1998,1,1}\r";
const char * LabPro::k_sResetForUpdate = "\n; ResetALL ResetAll ResetAll ResetAll \n";
const char * LabPro::k_sUpdateOSCmd = "; Ving Base Code - Hi Rob\n";
const char * LabPro::k_sUpdateUserImageCmd = "; Ving User Image ";
const char * LabPro::k_sFirmwareBaseName = "base-dm*.hex";
const char * LabPro::k_GetArchiveInfo = "s{201,1,0,0}\r";
const char * LabPro::k_CommandOkayResponse = "; OK\r\n";
const char * LabPro::k_sGetDataCmd = "g\r";
const char * LabPro::k_SetBinaryModeCmd = "s{4,0,-1}\r";
const char * LabPro::k_SetTextModeCmd = "s{4,0,0}\r";
const char * LabPro::k_SetBaudRate115 = "s{105,115}\r";
const char * LabPro::k_GetMemoryInfo = "s{201,1,0,0,83.1}\r";

const int LabPro::k_nSetupChannelCmdValue = 1;
const int LabPro::k_nTriggerCmdValue = 3;
const int LabPro::k_nEquationSetupCmdValue = 4;
const int LabPro::k_nDataRetrievalCmdValue = 5;
const int LabPro::k_nChannelStatusCmdValue = 8;
const int LabPro::k_nChannelDataCmdValue = 9;
const int LabPro::k_nDataReductionCmdValue = 10;
const int LabPro::k_nDigitalCollectCmdValue = 12;
const int LabPro::k_nPowerControlCmdValue = 102;
const int LabPro::k_nMotionDetectorUndersampleCmdValue = 106;
const int LabPro::k_nAnalogOversampleBurstCmdValue = 107;
const int LabPro::k_nReadDDSBlockCmdValue = 110;
const int LabPro::k_nWriteDDSBlockCmdValue = 111;
const int LabPro::k_nRequestSetupCmdValue = 115;
const int LabPro::k_nGetDDSLongNameCmdValue = 116;
const int LabPro::k_nGetDDSShortNameCmdValue = 117;
const int LabPro::k_nChangeDDSCalPageCmdValue = 119;
const int LabPro::k_nArchiveOpCmdValue = 201;
const int LabPro::k_nAnalogOutCmdValue = 401;
const int LabPro::k_nLEDCmdValue = 1998;
const int LabPro::k_nSoundCmdValue = 1999;
const int LabPro::k_nDigitalOutCmdValue = 2001;

const int LabPro::k_nUnlimitedPoints = -1; 
const int LabPro::k_nDigitalDataChannelOne = 41;
const int LabPro::k_nDigitalDataChannelTwo = 42;

const int LabPro::k_nMinimumDDSSensorID = 20;

const char * LabPro::k_sBadDataPointText = "{  -9.99900E+02 }";
const real LabPro::k_fBadDataPointValue = -999.9;
const real LabPro::k_fFastestRealtimeRate = 0.004;	// 250 pts/s
const real LabPro::kfMaxDeltaT = 16000; 

const int LabPro::k_nMaxRemotePoints = 12000;

*/

/////////////////////////////////////////////////////////////////////////////
// CMainFrame

IMPLEMENT_DYNCREATE(CMainFrame, CFrameWnd)

BEGIN_MESSAGE_MAP(CMainFrame, CFrameWnd)
	//{{AFX_MSG_MAP(CMainFrame)
	ON_WM_CREATE()
	ON_COMMAND(IDM_CONNECT, OnConnect)
	ON_UPDATE_COMMAND_UI(IDM_CONNECT, OnUpdateConnect)
	ON_COMMAND(IDM_DISCONNECT, OnDisconnect)
	ON_UPDATE_COMMAND_UI(IDM_DISCONNECT, OnUpdateDisconnect)
	ON_COMMAND(IDM_GETSTATUS, OnGetstatus)
	ON_UPDATE_COMMAND_UI(IDM_GETSTATUS, OnUpdateGetstatus)
	ON_COMMAND(IDM_RECONNECT, OnReconnect)
	ON_UPDATE_COMMAND_UI(IDM_RECONNECT, OnUpdateReconnect)
	ON_COMMAND(IDM_SETNUMCHANNELS, OnSetnumchannels)
	ON_UPDATE_COMMAND_UI(IDM_SETNUMCHANNELS, OnUpdateSetnumchannels)
	ON_WM_TIMER()
	ON_COMMAND(ID_EDIT_PASTE, OnEditPaste)
	ON_UPDATE_COMMAND_UI(ID_EDIT_PASTE, OnUpdateEditPaste)
	//}}AFX_MSG_MAP
	ON_UPDATE_COMMAND_UI(ID_SEND_STRING_TO_LABPRO, OnUpdateSendButton)
	ON_BN_CLICKED(ID_SEND_STRING_TO_LABPRO, OnSendStringToLabPro)
END_MESSAGE_MAP()

static UINT indicators[] =
{
	ID_SEPARATOR,           // status line indicator
	ID_INDICATOR_CAPS,
	ID_INDICATOR_NUM,
	ID_INDICATOR_SCRL,
};

/////////////////////////////////////////////////////////////////////////////
// CMainFrame construction/destruction

CMainFrame::CMainFrame()
{
	m_timerId = 0;
}

CMainFrame::~CMainFrame()
{
}

int CMainFrame::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	if (CFrameWnd::OnCreate(lpCreateStruct) == -1)
		return -1;
	
	if (!m_wndStatusBar.Create(this) ||
		!m_wndStatusBar.SetIndicators(indicators,
		  sizeof(indicators)/sizeof(UINT)))
	{
		TRACE0("Failed to create status bar\n");
		return -1;      // fail to create
	}

	m_wndSendBar.Create(this, IDD_DIALOGBAR_SEND, CBRS_TOP, IDD_DIALOGBAR_SEND);
	m_wndSendBar.EnableDocking(CBRS_ALIGN_ANY);

	EnableDocking(CBRS_ALIGN_ANY);

	DockControlBar(&m_wndSendBar);

	CButton *pAppendCheckBox = (CButton *) m_wndSendBar.GetDlgItem(IDC_APPEND_0D);
	if (pAppendCheckBox)
		pAppendCheckBox->SetCheck(1);

	m_timerId = SetTimer(777, TIMER_PERIOD_MS, NULL);

	return 0;
}

BOOL CMainFrame::PreCreateWindow(CREATESTRUCT& cs)
{
	if( !CFrameWnd::PreCreateWindow(cs) )
		return FALSE;
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs

	return TRUE;
}

/////////////////////////////////////////////////////////////////////////////
// CMainFrame diagnostics

#ifdef _DEBUG
void CMainFrame::AssertValid() const
{
	CFrameWnd::AssertValid();
}

void CMainFrame::Dump(CDumpContext& dc) const
{
	CFrameWnd::Dump(dc);
}

#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CMainFrame message handlers


void CMainFrame::OnConnect() 
{
	LabProUSB_Open();
}

void CMainFrame::OnUpdateConnect(CCmdUI* pCmdUI) 
{
	BOOL bEnable = (LabProUSB_IsOpen() == 0);
	pCmdUI->Enable(bEnable);
}

void CMainFrame::OnDisconnect() 
{
	LabProUSB_Close();
}

void CMainFrame::OnUpdateDisconnect(CCmdUI* pCmdUI) 
{
	BOOL bEnable = (LabProUSB_IsOpen() != 0);
	pCmdUI->Enable(bEnable);
}

void CMainFrame::OnGetstatus() 
{
	CLabPro_consoleDoc *pDoc = (CLabPro_consoleDoc *) GetActiveDocument();
	if (pDoc)
	{
		ShipStringToLabPro("s\r");//Wake up.
		Sleep(10);

		ShipStringToLabPro("s{7}\r");//Request status..
		Sleep(20);
	}
}

void CMainFrame::OnUpdateGetstatus(CCmdUI* pCmdUI) 
{
	BOOL bEnable = (LabProUSB_IsOpen() != 0);
	pCmdUI->Enable(bEnable);
}

void CMainFrame::OnReconnect() 
{
	LabProUSB_Open();
}

void CMainFrame::OnUpdateReconnect(CCmdUI* pCmdUI) 
{
	BOOL bEnable = (LabProUSB_IsOpen() != 0);
	pCmdUI->Enable(bEnable);
}

void CMainFrame::OnSetnumchannels() 
{
	CLabPro_consoleDoc *pDoc = (CLabPro_consoleDoc *) GetActiveDocument( );
	if (pDoc)
	{
		CSetNumChansAndModesDlg dlg;
		dlg.m_bBinaryMode = pDoc->m_LabPro_bBinaryMode;
		dlg.m_bRealTimeMode = pDoc->m_LabPro_bRealTime;
		dlg.m_num_chans = pDoc->m_LabPro_nNumChannels;
		int status = dlg.DoModal();
		if (IDOK == status)
		{
			pDoc->m_LabPro_bBinaryMode = dlg.m_bBinaryMode;
			pDoc->m_LabPro_bRealTime = dlg.m_bRealTimeMode;
			pDoc->m_LabPro_nNumChannels = dlg.m_num_chans;
			LabProUSB_SetNumChannelsAndModes(pDoc->m_LabPro_nNumChannels, pDoc->m_LabPro_bBinaryMode, pDoc->m_LabPro_bRealTime);
		}
	}

}

void CMainFrame::OnUpdateSetnumchannels(CCmdUI* pCmdUI) 
{
	BOOL bEnable = (LabProUSB_IsOpen() != 0);
	pCmdUI->Enable(bEnable);
}

void CMainFrame::OnUpdateSendButton(CCmdUI* pCmdUI) 
{
	CString str;
	BOOL bEnable = (LabProUSB_IsOpen() != 0) 
		&& (m_wndSendBar.GetDlgItemText(IDC_EDIT_LABPRO_XMIT_STRING, str) > 0);
	pCmdUI->Enable(bEnable);
}

BOOL CMainFrame::DestroyWindow() 
{
	if (m_timerId != 0)
		KillTimer(m_timerId);
	m_timerId = 0;

	return CFrameWnd::DestroyWindow();
}

void CMainFrame::OnTimer(UINT nIDEvent) 
{
	CLabPro_consoleDoc *pDoc = (CLabPro_consoleDoc *) GetActiveDocument();
	CLabPro_consoleView *pView = (CLabPro_consoleView *) GetActiveView();
	if (pDoc && pView)
	{
		if (LabProUSB_IsOpen())
		{
			long available_bytes = LabProUSB_GetAvailableBytes();
			long bytes_read = available_bytes; 
			if (available_bytes > 0)
			{
				ASSERT((available_bytes + pDoc->m_input_buffer_next_byte_index) < MAX_INPUT_BUFLEN);
				if ((available_bytes + pDoc->m_input_buffer_next_byte_index) < MAX_INPUT_BUFLEN)
				{
					if (0 == LabProUSB_ReadBytes(&bytes_read, 
						&(pDoc->m_input_buffer[pDoc->m_input_buffer_next_byte_index])))
					{
						pDoc->m_input_buffer_next_byte_index += bytes_read;
						int new_list_recs = pDoc->m_input_buffer_next_byte_index/REPORT_RECORD_DATA_LENGTH;
						if (new_list_recs > 0)
						{
							pView->RecordLabProInput(pDoc->m_input_buffer, new_list_recs*REPORT_RECORD_DATA_LENGTH);
							memcpy(pDoc->m_input_buffer, &(pDoc->m_input_buffer[new_list_recs*REPORT_RECORD_DATA_LENGTH]),
								pDoc->m_input_buffer_next_byte_index % REPORT_RECORD_DATA_LENGTH);
							pDoc->m_input_buffer_next_byte_index = 
								pDoc->m_input_buffer_next_byte_index % REPORT_RECORD_DATA_LENGTH;
						}
					}
				}
			}
			else if (pDoc->m_input_buffer_next_byte_index > 0)
			{
				pView->RecordLabProInput(pDoc->m_input_buffer, pDoc->m_input_buffer_next_byte_index);
				pDoc->m_input_buffer_next_byte_index = 0;
			}
		}
		else if (pDoc->m_input_buffer_next_byte_index > 0)
		{
			pView->RecordLabProInput(pDoc->m_input_buffer, pDoc->m_input_buffer_next_byte_index);
			pDoc->m_input_buffer_next_byte_index = 0;
		}
	}
}

int CMainFrame::ShipBytesToLabPro(LPCSTR pBytes, int num_bytes)
{
	short N = num_bytes;
	int status = LabProUSB_WriteBytes(&N, (LPSTR) pBytes);
	if (0 == status)
	{
		CLabPro_consoleView *pView = (CLabPro_consoleView *) GetActiveView();
		if (pView)
		{
			pView->RecordLabProOutput(pBytes, N);
		}
	}
	return(status);
}

void CMainFrame::OnSendStringToLabPro()
{
	CString str;
	BOOL bEnable = (LabProUSB_IsOpen() != 0) 
		&& (m_wndSendBar.GetDlgItemText(IDC_EDIT_LABPRO_XMIT_STRING, str) > 0);
	if (bEnable)
	{
		CButton *pAppendCheckBox = (CButton *) m_wndSendBar.GetDlgItem(IDC_APPEND_0D);
		if (pAppendCheckBox)
		{
			if (pAppendCheckBox->GetCheck() != 0)
				str += "\r";
		}

		ShipStringToLabPro((LPCSTR) str);
		m_wndSendBar.SetDlgItemText(IDC_EDIT_LABPRO_XMIT_STRING, "");
	}
}

void CMainFrame::OnUpdateFrameTitle(BOOL bAddToTitle)
{
	SetWindowText("LabPro_console");
}

void CMainFrame::OnEditPaste() 
{
    if (::OpenClipboard(m_hWnd)) 
	{
		HANDLE hClipData = NULL;
		LPSTR lpClipData = NULL;

        /* get text from the clipboard */
        hClipData = GetClipboardData(CF_TEXT);

        if (hClipData)
		{
			lpClipData = (LPSTR) GlobalLock(hClipData);
			m_wndSendBar.SetDlgItemText(IDC_EDIT_LABPRO_XMIT_STRING, lpClipData);
        
			GlobalUnlock(hClipData);
		}
        CloseClipboard();
    }
}

void CMainFrame::OnUpdateEditPaste(CCmdUI* pCmdUI) 
{
	BOOL bEnable = IsClipboardFormatAvailable(CF_TEXT);
	pCmdUI->Enable(bEnable);
}

BOOL CMainFrame::PreTranslateMessage(MSG* pMsg) 
{
	BOOL bTranslated = FALSE;
	CString str;
    if ((pMsg->message==WM_KEYDOWN) && (pMsg->wParam==VK_RETURN))
	{
		//If ready to send cmd, then do so.
		if ((LabProUSB_IsOpen() != 0) 
			&& (m_wndSendBar.GetDlgItemText(IDC_EDIT_LABPRO_XMIT_STRING, str) > 0))
		{
			OnSendStringToLabPro();
			bTranslated = TRUE;
		}
	}

	if (!bTranslated)
		bTranslated = CFrameWnd::PreTranslateMessage(pMsg);
	
	return bTranslated;
}

