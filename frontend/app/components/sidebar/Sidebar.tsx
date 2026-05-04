'use client'
import React from 'react'
import HeaderButton from '../header/HeaderButton'

interface SidebarProps {
    buttons: string[];
    isOpen: boolean;
    onClose: () => void;
}

const Sidebar = (p: SidebarProps) => {
  return (
    <>
      {/* overlay */}
      <div
        className={`fixed inset-0 z-20 md:hidden
            bg-theme1/60 
            backdrop-filter backdrop-blur-xl 
            transition-opacity duration-300 ease-in-out ${p.isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}
        onClick={p.onClose}
        aria-hidden="true"
      />

      {/* sidebar */}
      <div
        className={`fixed top-0 right-0 h-full w-1/2 p-2 z-30 md:hidden
            bg-theme1 border-l border-theme4 shadow-lg 
            transform transition-transform duration-300 ease-in-out ${p.isOpen ? 'translate-x-0' : 'translate-x-full'}`}
        role="dialog"
        aria-modal="true"
        aria-labelledby="sidebar-title"
      >
        <div className="flex flex-col gap-2">
            <div className='h-12 flex justify-end'>
                <button onClick={p.onClose} className="h-full aspect-square flex justify-center items-center p-1 rounded-full border border-transparent transition cursor-pointer hover:bg-theme2 hover:text-text hover:border-highlight">
                    <svg width="1.5em" height="1.5em" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                    <span className="sr-only">Close menu</span>
                </button>
            </div>
            {p.buttons.map(s => <HeaderButton key={s} content={s} onClick={p.onClose}/>)}
        </div>
      </div>
    </>
  )
}

export default Sidebar