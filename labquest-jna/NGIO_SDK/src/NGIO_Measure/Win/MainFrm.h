// MainFrm.h : interface of the CMainFrame class
//


#pragma once

class CMainFrame : public CFrameWnd
{
	
protected: // create from serialization only
	CMainFrame();
	DECLARE_DYNCREATE(CMainFrame)

// Attributes
public:
	void UpdateUnits();
	void ClearGraph();
	void ResetDevice()
	{
		OnStopMeas();
		OnAnalog1();
	}

// Operations
public:

// Overrides
public:
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	virtual BOOL DestroyWindow();

// Implementation
public:
	virtual ~CMainFrame();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

	CDialogBar	m_wndSendBar;

protected:  // control bar embedded members
	CStatusBar  m_wndStatusBar;
//	CToolBar    m_wndToolBar;

// Generated message map functions
protected:
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	afx_msg void OnInitMenuPopup(CMenu* pPopupMenu, UINT nIndex, BOOL bSysMenu);
	afx_msg void OnDevice0();
	afx_msg void OnDevice1();
	afx_msg void OnDevice2();
	afx_msg void OnDevice3();
	afx_msg void OnDevice4();
	afx_msg void OnDevice5();
	afx_msg void OnDevice6();
	afx_msg void OnDevice7();
	afx_msg void OnTimer(UINT_PTR nIDEvent);
	afx_msg void OnUpdateGetStatus(CCmdUI* pCmdUI);
	afx_msg void OnGetStatus();
	afx_msg void OnGetSensorId();
	afx_msg void OnUpdateGetSensorId(CCmdUI* pCmdUI);
	afx_msg void OnUpdateSetMeasPeriod(CCmdUI* pCmdUI);
	afx_msg void OnSetMeasPeriod();
	afx_msg void OnUpdateStartMeas(CCmdUI* pCmdUI);
	afx_msg void OnStartMeas();
	afx_msg void OnUpdateStopMeas(CCmdUI* pCmdUI);
	afx_msg void OnStopMeas();
	afx_msg void OnCalib0();
	afx_msg void OnCalib1();
	afx_msg void OnCalib2();
	afx_msg void OnAnalog1();
	afx_msg void OnAnalog2();
	afx_msg void OnAnalog3();
	afx_msg void OnAnalog4();
	afx_msg void OnChannelsDigital1();
	afx_msg void OnChannelsDigital2();
	afx_msg void OnAnalog110v();
	afx_msg void OnChannelsDigital1Motion();
	afx_msg void OnChannelsDigital2Motion();
	afx_msg void OnChannelsDigital1Rotary();
	afx_msg void OnChannelsDigital1Radiation();
	afx_msg void OnChannelsDigital2Rotary();
	afx_msg void OnChannelsDigital2Radiation();
	afx_msg void OnChannelsDigital1RotaryX4();
	afx_msg void OnChannelsDigital2RotaryX4();
//	afx_msg void OnUpdateChannelsAudioInternal(CCmdUI* pCmdUI);
	afx_msg void OnChannelsAudioInternal();
	afx_msg void OnUpdateNgioLibVerbose(CCmdUI* pCmdUI);
	afx_msg void OnNgioLibVerbose();
	afx_msg void OnUpdateActionSetdisplaydepth(CCmdUI* pCmdUI);
	afx_msg void OnActionSetdisplaydepth();
	DECLARE_MESSAGE_MAP()

	void OnDeviceN(unsigned int N);
	void OnCalibN(unsigned int N);
	void SetLEDColor(int color, int brightness);

	UINT_PTR m_timerId;
	bool m_bIsCollectingMeasurements;
};


