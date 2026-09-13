import Link from 'next/link'
import React from 'react'
import { buttonVariants } from '../ui/button';
import { cn } from '@/lib/utils';

interface HeaderButtonProps {
    link?: string,
    content: string,
    onClick?: () => void;
}

const HeaderButton = ({ link=undefined, content, onClick }: HeaderButtonProps) => {
  return (
    <Link 
      href={link ?? `/${content.toLowerCase()}`} 
      onClick={onClick} 
      className={cn(buttonVariants({ variant: 'blended' }), 'h-full px-4 py-2')}
    >
      {content}
    </Link>
  )
}

export default HeaderButton