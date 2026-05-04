import Link from 'next/link'
import React from 'react'

interface HeaderButtonProps {
    link?: string,
    content: string,
    onClick?: () => void;
}

const HeaderButton = ({ link=undefined, content, onClick }: HeaderButtonProps) => {
  return (
    <Link href={link ?? `/${content.toLowerCase()}`} onClick={onClick} className='pl-4 pr-4 pt-2 pb-2 rounded border border-transparent transition hover:bg-theme2 hover:text-text hover:border-highlight active:text-highlight'>
        {content}
    </Link>
  )
}

export default HeaderButton