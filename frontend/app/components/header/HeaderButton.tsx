import Link from 'next/link'
import React, { ReactNode } from 'react'

interface HeaderButtonProps {
    link:string,
    content:ReactNode,
    onClick?: () => void;
}

const HeaderButton = (p:HeaderButtonProps) => {
  return (
    <Link href={p.link} onClick={p.onClick} className='pl-4 pr-4 pt-2 pb-2 rounded-full border border-transparent transition hover:bg-theme2 hover:text-text hover:border-highlight'>
        {p.content}
    </Link>
  )
}

export default HeaderButton