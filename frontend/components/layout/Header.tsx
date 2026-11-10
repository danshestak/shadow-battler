'use client'
import React, { useState } from 'react'
import HeaderButton from './HeaderButton'
import Link from 'next/link'
import Image from 'next/image'
import HamburgerButton from './HamburgerButton'
import Sidebar from '../sidebar/Sidebar'

const Header = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const buttons = ["Opponents", "Counters", "Battle"];

  //backdrop-filter backdrop-blur-lg
  return (
    <>
      <header className='
      sticky top-0 z-10 h-16 px-4 py-2
      flex justify-between 
      bg-theme1 border-b border-theme4 shadow-lg'>
        {/* left */}
        <Link className='flex items-center h-full text-2xl gap-0.5 tracking-tighter font-light' href="/">
          <Image src="/logo.svg" width={32} height={32} alt="Shadow Battler logo"/>
          <div>Shadow Battler</div>
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