'use client'
import React, { useState } from 'react'
import HeaderButton from './HeaderButton'
import Link from 'next/link'
import HamburgerButton from './HamburgerButton'
import Sidebar from '../sidebar/Sidebar'

const Header = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const buttons = ["Opponents", "Counters", "Battle"];

  return (
    <>
      <header className='
      sticky top-0 z-10 h-16 p-2
      flex justify-between 
      backdrop-filter backdrop-blur-lg backdrop-saturate-200 
      bg-theme1/90 border-b border-theme4 shadow'>
        {/* left */}
        <Link className='flex items-center h-full text-3xl font-thin tracking-tight' href="/">
          Shadow<span className='text-highlight font-semibold'>KO</span>
        </Link>

        {/* right */}
        <div className='flex'>
          <HamburgerButton onOpen={() => setIsSidebarOpen(true)} isOpen={isSidebarOpen} />
          <div className='hidden md:flex items-center gap-2'>
            {buttons.map(s => <HeaderButton key={s} content={s}/>)}
          </div>
        </div>
      </header>
      <Sidebar buttons={buttons} isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />
    </>
  )
}

export default Header