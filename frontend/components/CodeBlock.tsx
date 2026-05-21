import React from 'react';

interface CodeBlockProps {
  children: React.ReactNode;
}

const CodeBlock = ({ children }: CodeBlockProps) => {
  return <code className='bg-theme3 p-0.5 rounded border border-theme4 text-sm'>{children}</code>;
};

export default CodeBlock;