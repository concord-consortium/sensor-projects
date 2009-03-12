// GoIO_consoleDoc.h : interface of the CGoIO_consoleDoc class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_GOIO_CONSOLEDOC_H__8FDFDA77_BC76_45A1_AFCD_CB8D9A631AE5__INCLUDED_)
#define AFX_GOIO_CONSOLEDOC_H__8FDFDA77_BC76_45A1_AFCD_CB8D9A631AE5__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

//#include "GoIO_DLL_interface.h"
#include "GCircularBuffer.h"

#define MAX_NUM_MEASUREMENTS_IN_CIRBUF 1001

class CGoIO_consoleDoc : public CDocument
{
protected: // create from serialization only
	CGoIO_consoleDoc();
	DECLARE_DYNCREATE(CGoIO_consoleDoc)

// Attributes
public:
	NGIO_DEVICE_HANDLE GetOpenDevicePtr() 
	{ 
		if (m_pAudioDevice)
			return m_pAudioDevice; 
		else
			return m_pDevice;
	}
	NGIO_DEVICE_HANDLE GetParentDevicePtr() { return m_pDevice; }
	NGIO_DEVICE_HANDLE GetAudioDevicePtr() { return m_pAudioDevice; }
	unsigned char GetActiveChannel() { return m_activeChannel; }
	void SetActiveChannel(unsigned char channel) { m_activeChannel = channel; }

	void Set10voltRangeFlag(bool flag)
	{
		m_b10voltRange = flag;
	}
	bool Get10voltRangeFlag()
	{
		return m_b10voltRange;
	}

	void SetDigitalCollectionMode(int mode)
	{
		m_digitalCollectionMode = mode;
	}
	int GetDigitalCollectionMode()
	{
		return m_digitalCollectionMode;
	}

	unsigned char UpdateSensorIdAndDDSRec(unsigned char channel);//Returns sensorId after querying device.
	void UpdateTitle();

// Operations
public:
	NGIO_DEVICE_HANDLE OpenDevice(LPCSTR pDeviceName);
	void CloseDevice();
	NGIO_DEVICE_HANDLE OpenAudioDevice();
	void CloseAudioDevice();

	void AddMeasurementToCirbuf(double measurement, double time);
	int GetNumMeasurementsInCirbuf() { return m_pMeasCirBuf->NumBytesAvailable()/sizeof(double); }
	void ClearMeasurementCirbuf();
	bool GetNthMeasurementInCirbuf(int N, double *pMeasurement, double *pTime); //(N == 0) => first measurement.
	void SetMeasurementPeriodInSeconds(double period) { m_measPeriodInSeconds = period; }
	double GetMeasurementPeriodInSeconds() { return m_measPeriodInSeconds; }

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGoIO_consoleDoc)
	public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CGoIO_consoleDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	//{{AFX_MSG(CGoIO_consoleDoc)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()


	NGIO_DEVICE_HANDLE m_pDevice;
	NGIO_DEVICE_HANDLE m_pAudioDevice;
//	double m_measurements_cirbuf[MAX_NUM_MEASUREMENTS_IN_CIRBUF];
	int m_numMeasurementsInCirbuf;
	int m_firstCirbufMeasurementIndex;
	double m_measPeriodInSeconds;
	GCircularBuffer *m_pMeasCirBuf;
	GCircularBuffer *m_pTimeCirBuf;
	unsigned char m_activeChannel;
	bool m_b10voltRange;
	int m_digitalCollectionMode;
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_GOIO_CONSOLEDOC_H__8FDFDA77_BC76_45A1_AFCD_CB8D9A631AE5__INCLUDED_)
